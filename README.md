grails-websockets-blackjack
===========================

This is an example project I made for Grails using websockets and AngularJS.

Install and play a collaborated blackjack game!
Requires Grails version 2.4.0+ (needs Spring 4+)

Deployed Version:
http://ec2-54-191-55-56.us-west-2.compute.amazonaws.com/blackjack/

This code exemplifies

1. hybrid usage of AngularJS / Grails GSP

2. Websockets and Ajax


Still left to do:

1. Authenticate users who subscribed to socket feed

2. Conform the backend to Observer pattern using Spring 4 tools

3. Might want to find a better alternative to "Thread.sleep" for enabling the timer

4. Missing Unit Tests/Integration Test

5. Track how many loses/wins per player
