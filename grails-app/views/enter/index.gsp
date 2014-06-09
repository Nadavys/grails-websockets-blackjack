<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <asset:javascript src="jquery" />
    %{--<asset:javascript src="spring-websocket" />--}%

    <script type="text/javascript">
    /*    $(function() {
            var socket = new SockJS("${createLink(uri: '/stomp')}");
            var client = Stomp.over(socket);

            client.connect({}, function() {
                client.subscribe("/topic/pushGameStatus", function(message) {
                    var data = JSON.parse(message.body);
                    console.info("message", data);
                    console.info("message", data.command);
//                    $("#eventList").append("<li>new event: " + data.id+ "</li>");
                    switch (data.command){
                        case 'timeToNewGame':
                            $('#countdownToNewGame').html(data.time)
                            break;
                    }
                });
            });
        });
    */
    </script>
</head>

<body>




<!-- Main jumbotron for a primary marketing message or call to action -->
<div class="jumbotron">
    <h1>Daddy Needs A New Pair of Shoes!</h1>
    <p>Enter your name and join the blackjack Table</p>
    <p><a href="#" class="btn btn-primary btn-lg" role="button">Learn more &raquo;</a></p>
</div>


    <div class="row">
        <form action="" method="post">
            <input type="hidden" name="isSubmit" value="true"/>
            <div class="form-group">
            <label>Player Name</label>
         <input name="playerName" class="form-control" value="${playerName}"/>
                </div>

          <div class="form-group">

          <button class="btn btn-lg btn-primary" type="submit">Enter Game</button>
          </div>
        </form>
        </div>
</body>
</html>