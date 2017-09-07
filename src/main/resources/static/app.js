var stompClient = null;
var ctx;
var gameData;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function update() {

    for (i in gameData.foods) {
        ctx.beginPath();
        ctx.arc(gameData.foods[i].x, gameData.foods[i].y, 1, 0, 2 * Math.PI);
        ctx.fill();
    }

}


function connect() {
    var socket = new SockJS('/game');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/login', function (serverMessage) {
            var response = JSON.parse(serverMessage.body);
            showLog(response.message);
            if (response.code === 1) {
                gameData = response.data;
                update();
            }
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/login", {}, JSON.stringify({'name': $("#name").val(), 'type': 'login'}));
}

function showLog(message) {
    ctx.fillText(message, 10, 10);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });

    var canvas = document.getElementById("canvas");
    ctx = canvas.getContext("2d");
});

