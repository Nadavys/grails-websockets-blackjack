package grails.stomp

import gameEntities.Hand
import groovy.transform.ToString

@ToString()
class GamePlayer {
    String name
    Hand hand
    Integer credits = 0
    Long internalId = 0
    String imageUrl = this.randomAvatar()

    static constraints = {
        name(required: true, blank: false)
        hand(required: false)
        credits(required: false)
    }

    static randomAvatar(){
        Random random = new Random()
        sprintf( 'http://api.randomuser.me/portraits/%s/%s.jpg', [random.nextBoolean()?'men': 'women',random.nextInt(90)+1] )
    }

    static transients = ['hand']
}



