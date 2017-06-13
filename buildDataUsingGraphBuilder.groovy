#!/usr/bin/env groovy
package com

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode
import java.text.SimpleDateFormat
import org.codehaus.groovy.runtime.StringGroovyMethods
import static MyObjectGraphBuilder.*

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

class MyObjectGraphBuilder extends ObjectGraphBuilder{
    def defaults
    static final initWithDefaults = "INIT"

    MyObjectGraphBuilder(defaults){
        super()
        this.defaults = defaults
        this.addPostNodeCompletionDelegate{a, parent, node -> setDefaults(node) }
    }
    
   def setDefaults(object){
        def searchKey = (object.class.name - "com.").toLowerCase()
        defaults[searchKey].each{key, value ->
            if(object."$key" == null) {
                if(value == initWithDefaults){
                    def className = this.classNameResolver.resolveClassname(key)
                    def newObject = this.class.classLoader.loadClass( className, true, false )?.newInstance()
                    setDefaults(newObject)
                    object."$key" = newObject
                } else{
                    object."$key" = value
                }
            }
        }
   }
}

use(ConvertStringToDateCategory, DurationCategory){
    def defaults = [
        user: [
            firstName: "A",
            lastName: "B"
        ],
        organizer: [
            user: initWithDefaults 
        ],
        event: [
            name: "Awesome event",
            organizer: initWithDefaults,
            date: new Date(),
            duration: 1.days
        ] 
    ]

    def builder = new MyObjectGraphBuilder(defaults)
    builder.classLoader = this.class.classLoader                    
    builder.classNameResolver = "com" 

    def anEvent = builder.with{
        event(
            date: "23/10/2016" as Date,
            duration: 2.days
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
