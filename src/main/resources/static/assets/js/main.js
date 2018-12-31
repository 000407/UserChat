'use strict';

var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var usersList = document.querySelector('#usersList');
var connectingElement = document.querySelector('.connecting');
var conversations = {};
const me = document.querySelector("#username").value;

var stompClient = null;
var username = null;

var Conversation = function(messages){
    this.messages = messages;
    this.populate = function(){
        messageArea.innerHTML = "";
        for(var i in this.messages){
            var message = this.messages[i];

            var messageElement = document.createElement('li');
            var container = document.createElement("span");
            messageElement.classList.add('chat-message');

            if(message.sender === me)
                container.classList.add("to-me");
            else
                container.classList.add("from-me");

            var textElement = document.createElement('p');
            var messageText = document.createTextNode(message.content);
            textElement.appendChild(messageText);

            container.appendChild(textElement);
            messageElement.appendChild(container);

            messageArea.appendChild(messageElement);
            messageArea.scrollTop = messageArea.scrollHeight;
        }
    };

    this.addMessage = function (message) {
        this.messages.push(message);
    }
};

var ChatMessage = function(){

};

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

$(document).ready(function () {
    connect();

    $(document).on("click", "li.users", function(){
        $("li.users").removeClass("user-active");
        $(this).removeClass("new-message").addClass("user-active");
        $("#activeRecipient").val($(this).data("name")).trigger("change");
    });

    $(document).on("change", "#activeRecipient", function(){
        var recip = $(this).val();
        var conversation = conversations[recip];
        if(!conversation){
            conversation = new Conversation([]);
            conversations[recip] = conversation;
        }
        conversation.populate();
    });
});

function connect() {
    username = document.querySelector('#username').value.trim();

    if(username) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
}


function onConnected() {
    // Subscribe to the topics
    stompClient.subscribe('/users/online', onUserListUpdated);
    stompClient.subscribe('/user/users/online', onUserListUpdated);
    stompClient.subscribe('/user/queue/new', onMessageReceived);

    // Request the list of online users at the moment
    stompClient.send("/userchat/users/online",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    //connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    var recipient = $("#activeRecipient").val();
    if(messageContent && recipient && stompClient) {
        var chatMessage = {
            sender: username,
            recipient: recipient,
            content: messageInput.value,
            type: 'CHAT'
        };
        conversations[recipient].addMessage(chatMessage);
        conversations[recipient].populate();
        stompClient.send("/userchat/chat/send", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onUserListUpdated(payload) {
    var message = JSON.parse(payload.body);
    var onlineUsers = JSON.parse(message.content);

    usersList.innerHTML = ""; //TODO: This could be problematic. Change.

    for(var i in onlineUsers){
        var usr = onlineUsers[i];

        if(usr === me)
            continue;

        var userElement = document.createElement('li');

        userElement.setAttribute("data-name", usr);

        userElement.classList.add('user');
        userElement.classList.add('users');
        userElement.id = "user_" + usr;

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(usr[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(usr);

        userElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(usr);
        usernameElement.appendChild(usernameText);
        userElement.appendChild(usernameElement);
        usersList.appendChild(userElement);
    }

    /*var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);*/
}


function onMessageReceived(payload){
    var message = JSON.parse(payload.body);

    if(!conversations[message.sender]){
        conversations[message.sender] = new Conversation([]);
    }

    var conv = conversations[message.sender];
    conv.addMessage(message);

    var activeRecipient = $("#activeRecipient").val();

    if(message.sender === activeRecipient){
        conv.populate();
    }
    else{
        $("#user_" + message.sender).addClass("new-message")
    }
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

messageForm.addEventListener('submit', sendMessage, true)