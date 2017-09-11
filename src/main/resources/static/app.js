var stompClient = null;
var ctx;
var gameData;
var playerId = 0;

var globalWidth = 800;
var globalHeight = 800;

var visibleWidth = 400;
var visibleHeight = 400;

var speed = 20;
var mousePosition;

var lastSelfUpdateTime;

var logMessage;


function setConnected(connected) {
    $("#send").prop("disabled", connected);
    $("#name").prop("disabled", connected);
}

function update() {
    //clean canvas
    ctx.clearRect(0, 0, visibleWidth, visibleHeight);
    var left = gameData.player.x - visibleWidth / 2;
    var top = gameData.player.y - visibleHeight / 2;
    var right = left + visibleWidth;
    var bottom = top + visibleHeight;
    //draw wall
    ctx.fillStyle = "#795548";
    if (left < 0) {
        ctx.fillRect(0, 0, -left, visibleHeight);
    }

    if (right > globalWidth) {
        ctx.fillRect(visibleWidth - (right - globalWidth), 0, right - globalWidth, visibleHeight);
    }

    if (top < 0) {
        ctx.fillRect(0, 0, visibleWidth, -top);
    }

    if (bottom > globalHeight) {
        ctx.fillRect(0, visibleHeight - (bottom - globalHeight), visibleWidth, bottom - globalHeight);
    }

    //draw food
    ctx.fillStyle = "#009688";
    for (i in gameData.foods) {
        ctx.beginPath();
        ctx.arc(gameData.foods[i].x - left, gameData.foods[i].y - top, gameData.foods[i].radius, 0, 2 * Math.PI);
        ctx.fill();
    }

    //draw other
    ctx.fillStyle = "#9c27b0";
    for (i in gameData.others) {
        ctx.beginPath();
        ctx.arc(gameData.others[i].x - left, gameData.others[i].y - top, gameData.others[i].radius, 0, 2 * Math.PI);
        ctx.fill();
    }

    //draw self
    ctx.fillStyle = "#e91e63";
    ctx.beginPath();
    ctx.arc(gameData.player.x - left, gameData.player.y - top, gameData.player.radius, 0, 2 * Math.PI);
    ctx.fill();

    //draw log
    ctx.fillText(logMessage,10,10);
}

function selfUpdate() {

    var timeElapse = new Date().getTime() - lastSelfUpdateTime;
    movePlayer(timeElapse, gameData.player);

    for (i in gameData.others) {
        movePlayer(timeElapse, gameData.others[i]);
    }
    update();

    lastSelfUpdateTime = new Date().getTime();
}


function movePlayer(timeElapse, player) {
    var deg;
    if (player.id === playerId) {
        deg = Math.atan2(mousePosition.y, mousePosition.x);
    } else {
        deg = Math.atan2(player.target.y, player.target.x);
    }

    player.x += Math.cos(deg) * timeElapse * speed / 1000;
    player.y += Math.sin(deg) * timeElapse * speed / 1000;

    if (player.x + player.radius > globalWidth) {
        player.x = globalWidth - player.radius;
    }

    if (player.x - player.radius < 0) {
        player.x = player.radius;
    }

    if (player.y + player.radius > globalHeight) {
        player.y = globalHeight - player.radius;
    }

    if (player.y - player.radius < 0) {
        player.y = player.radius;
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

            if (response.code === 1) {
                //setInterval("selfUpdate()", 50);
                setInterval("sendTarget()",200);
                //showLog(response.message + " " + response.data.name);
                if ($("#name").val() === response.data.name) { //名称和自己相同
                    playerId = response.data.id;
                    stompClient.subscribe('/user/' + playerId + '/update', function (serverMessage) {
                        var response = JSON.parse(serverMessage.body);
                        if (response.code === 1) {
                            gameData = response.data;
                            update();

                        } else {

                        }

                    })
                }
            } else if (response.code === 0) {
                showLog(response.message);
            }
        });
        stompClient.subscribe('/topic/broadcast', function (serverMessage) {
            var response = JSON.parse(serverMessage.body);
            showLog(response.message);
        });

        stompClient.send("/app/login", {}, JSON.stringify({'message': $("#name").val(), 'type': 'login'}));
    });
}

function sendTarget() {
    if (stompClient !== null && playerId !== 0 &&mousePosition!==null) {
        stompClient.send("/app/setTarget", {}, JSON.stringify({'id': playerId, 'target': mousePosition}));
    }
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    if ($("#name").val().length > 0) {
        connect();
    }

}


function showLog(message) {
    logMessage = message;
}

function getMousePos(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: evt.clientX - (rect.right - rect.width / 2),
        y: evt.clientY - (rect.bottom - rect.height / 2)
    };
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#send").click(function () {
        sendName();
    });

    var canvas = document.getElementById("canvas");
    canvas.height = visibleHeight;
    canvas.width = visibleWidth;
    canvas.addEventListener('mousemove', function (evt) {
        var mousePos = getMousePos(canvas, evt);
        mousePosition = mousePos;
        //var message = 'Mouse position: ' + mousePos.x + ',' + mousePos.y;
        //console.log(message);
    });
    ctx = canvas.getContext("2d");


});

