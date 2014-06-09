<span ng-switch="getSuit()">
    <div ng-class="getClass()" ng-switch-when="hearts">
        {{getName()}}<span compile="heart"></span>
    </div>
    <div ng-class="getClass()" ng-switch-when="spades">
        {{getName()}}<span compile="spade"></span>
    </div>
    <div ng-class="getClass()" ng-switch-when="clubs">
        {{getName()}}<span compile="club"></span>
    </div>
    <div ng-class="getClass()" ng-switch-when="diamonds">
        {{getName()}}<span compile="diamond"></span>
    </div>
</span>