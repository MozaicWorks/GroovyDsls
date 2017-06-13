#!/usr/bin/env groovy
import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode
import java.text.SimpleDateFormat
import org.codehaus.groovy.runtime.StringGroovyMethods

def event(Closure eventClosure){

    use(DurationCategory, ConvertStringToDateCategory){
        def newEvent = new Event()
        newEvent.with(eventClosure)
        return newEvent
    }
}

def anOrganizer(Closure organizerClosure){
    def newOrganizer = new Organizer()
    newOrganizer.with(organizerClosure)
    return newOrganizer
}

def anUser(Closure userClosure){
    def newUser = new User()
    newUser.with(userClosure)
    return newUser
}

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

anEvent = event {
    name = "Awesome event"
    date = "23/10/2016" as Date
    duration = 2.days
    organizer = anOrganizer{
        user = anUser{
            firstName = "A"
            lastName = "B"
        }
    }
}

use(ConvertStringToDateCategory){
    def anotherEvent = new Event(name: "Awesome event", date: "23/10/2016" as Date, duration: new Duration(numberOfDays: 2), organizer: new Organizer(user: new User(firstName: "A", lastName: "B"))) 
    assert anEvent == anotherEvent
}
