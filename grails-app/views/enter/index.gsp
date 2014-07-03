<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <asset:javascript src="jquery" />
    %{--<asset:javascript src="spring-websocket" />--}%

    <script type="text/javascript">
    </script>
</head>

<body>

<!-- Main jumbotron for a primary marketing message or call to action -->
<div class="jumbotron">
    <h1>Daddy Needs A New Pair of Shoes!</h1>
    <p>Enter your name and join the blackjack Table</p>
    <p><a href="http://en.wikipedia.org/wiki/Blackjack" class="btn btn-primary btn-lg" target="_blank" role="button">Learn To play BlackJack &raquo;</a></p>
</div>


    <div class="row">

        <g:hasErrors bean="${playerName}">
            <g:eachError><p><g:message error="${it}"/></p></g:eachError>
        </g:hasErrors>

        <form action="" method="post">
            <input type="hidden" name="isSubmit" value="true"/>
            <div class="form-group">
            <label>Player Name</label>
         <input name="playerName" class="form-control" value="playerName"/>
                </div>

          <div class="form-group">

          <button class="btn btn-lg btn-primary" type="submit">Enter Game</button>
          </div>
        </form>
        </div>
</body>
</html>