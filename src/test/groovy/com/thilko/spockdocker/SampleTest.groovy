package com.thilko.spockdocker

import groovy.sql.Sql
import spock.lang.Specification


@Docker(image = 'postgres',
        port = @PortBinding(hostIp = "127.0.0.1",
                hostPort = "5432",
                exposingPort = "5432/tcp"),
        readyString = "database system is ready to accept connections"
)
class SampleTest extends Specification {

    def "database is running"() {
        given:
        def dbUrl = "jdbc:postgresql://127.0.0.1:5432/postgres"
        def dbUser = "postgres"
        def dbPassword = "postgres"
        def dbDriver = "org.postgresql.Driver"

        when:
        def tryConnection = {
            try{
                Sql.newInstance(dbUrl, dbUser, dbPassword, dbDriver)

            }catch(e) {
                println e
                null
            }
        }

        def sql
        while(!(sql = tryConnection.call())){
            sleep(1000)
        }

        then:
        sql.rows('select * from pg_user;')
    }

    def cleanupSpec(){}

}
