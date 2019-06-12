package io.micronaut.data.jdbc

import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Shared
import spock.lang.Specification

import javax.inject.Inject
import javax.sql.DataSource

@MicronautTest
@Property(name = "datasources.default.name", value = "mydb")
class H2CrudSpec extends Specification {

    @Inject
    @Shared
    BookRepository bookRepository

    @Inject
    @Shared
    DataSource dataSource

    void setupSpec() {
        def conn = dataSource.getConnection()
        try {
            conn.prepareStatement('''
create table book (id bigint auto_increment, pages integer not null, title varchar(255), primary key (id))
''').execute()
        } finally {
            conn.close()
        }
    }


    void "test CRUD with JDBC"() {
        when:"we save a new book"
        def book = bookRepository.save(new Book(title: "The stand", pages: 1000))

        then:"The ID is assigned"
        book.id != null

        when:"The book is retrieved again"
        book = bookRepository.findById(book.id).orElse(null)

        then:"The book is back and valid"
        book.id != null
        book.title == "The stand"
        book.pages == 1000
    }
}
