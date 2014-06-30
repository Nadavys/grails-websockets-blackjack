package grails.stomp

import gameEntities.Hand
import groovy.transform.ToString

@ToString()
class GamePlayer {
    enum Type{ DEALER, PLAYER}

    String name
    Hand hand
    Integer credits = 0
    String imageUrl = this.randomAvatar()
    Type type

    static constraints = {
        name(required: true, blank: false)
        hand(required: false)
        credits(required: false)
        imageUrl(required: false, blank: true)
    }

    static randomAvatar(){
        Random random = new Random()
        sprintf( 'http://api.randomuser.me/portraits/%s/%s.jpg', [random.nextBoolean()?'men': 'women',random.nextInt(90)+1] )
    }

    static transients = ['hand']
}



