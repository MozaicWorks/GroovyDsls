#!/usr/bin/env groovy
package com

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode
import java.text.SimpleDateFormat
import org.codehaus.groovy.runtime.StringGroovyMethods

@EqualsAndHashCode
class Duration{
    def numberOfDays

    String toString(){
        return "$numberOfDays days"
    }
}

@Category(Number)
class DurationCategory{
    def getDays(){
        return new Duration(numberOfDays: this)
    }
}

@Category(String)
class ConvertStringToDateCategory{
    static final def convert = StringGroovyMethods.&asType

    static def asType(String self, Class clazz){
        if ( clazz  == Date){ 
            def format = new SimpleDateFormat("dd/MM/yyyy")
            return format.parse(self)
        }

        return convert(self, clazz)
    }
}


@ToString
@EqualsAndHashCode
class Event{
    String name
    Date date
    Duration duration
    Organizer organizer
}


@ToString
@EqualsAndHashCode
class Organizer{
    User user
}

@ToString
@EqualsAndHashCode
class User{
    String firstName
    String lastName
}


def setDefaults(User user){
    if(user.firstName == null) user.firstName = "A"
    if(user.lastName == null) user.lastName = "B"
}

def setDefaults(Organizer organizer){
    if(organizer.user == null){
        organizer.user = new User()
        setDefaults(organizer.user)
    }
}

def setDefaults(Event event){
    if(event.organizer == null){
        event.organizer = new Organizer()
        setDefaults(event.organizer)
    }

    if(event.name == null) event.name = "Awesome event"
    if(event.date == null) event.date = new Date()
    if(event.duration == null) event.duration = new Duration(numberOfDays: 1)
}

def setDefaults(Object object){
   // A default to avoid runtime errors 
}

use(ConvertStringToDateCategory, DurationCategory){
    def builder = new ObjectGraphBuilder()                          
    builder.classLoader = this.class.classLoader                    
    builder.classNameResolver = "com" 
    builder.addPostNodeCompletionDelegate{a, parent, node ->
        setDefaults(node)
    }

    def anEvent = builder.with{
        event(
            date: "23/10/2016" as Date,
            duration: 2.days,
        )
    }

    anotherEvent = new Event(
        name: "Awesome event", 
        date: "23/10/2016" as Date, 
        duration: new Duration(numberOfDays: 2), 
        organizer: new Organizer(
            user: new User(
                firstName: "A", 
                lastName: "B"
    ))) 

    assert anEvent == anotherEvent
}
