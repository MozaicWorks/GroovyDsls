#!/usr/bin/env groovy

// Using categories

class MeterDistance {
    def number

    MeterDistance plus(final MeterDistance second){
        return new MeterDistance(number: this.number + second.number)
    }

    String toString(){
        return "$number meters"
    }

    boolean equals(final MeterDistance other){
        return this.number == other.number 
    }
}


@Category(Number)
class MetersCategory {
    MeterDistance getMeters(){
        new MeterDistance(number: this)
    }

    MeterDistance getM(){
        getMeters()
    }

    MeterDistance getCm(){
        new MeterDistance(number: this/100)
    }
}

use(MetersCategory){
    assert 2.meters.toString() == "2 meters"
    println 2.meters

    assert 2.meters + 3.meters == 5.meters
    println 2.meters + 3.meters


    assert 2.m == 2.meters

    assert 2.m + 50.cm == 2.50.m
    println 2.m + 50.cm
}
