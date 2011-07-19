package org.moten.david.sowpods

object DbCreator {

  import scala.io.Source

  def create {
    println("loading words")
    val words = Source
      .fromInputStream(DbCreator.getClass().getResourceAsStream("/sowpods.txt"))
      .getLines
      .map(_.trim)
      .toList.toSet.toList.sorted

    println("creating database")
    import java.sql._
    Class.forName("org.sqlite.JDBC")
    val connection = DriverManager.getConnection("jdbc:sqlite:target/word.db")
    val stmt = connection.createStatement
    stmt.executeUpdate("drop table if exists android_metadata")
    stmt.executeUpdate("create table android_metadata (locale TEXT DEFAULT 'en_US');")
    stmt.executeUpdate("insert into android_metadata VALUES ('en_US');")
    stmt.executeUpdate("drop table if exists word;")
    stmt.executeUpdate("create table word(_id integer primary key,word,word_sorted);")
    val pstmt = connection.prepareStatement("insert into word values (?,?,?);")
    connection.setAutoCommit(false)
    var count = 0
    words.foreach(w => {
      count += 1
      pstmt.setInt(1, count)
      pstmt.setString(2, w)
      pstmt.setString(3, w.sorted)
      pstmt.addBatch
    })
    pstmt.executeBatch
    connection.commit
    stmt.executeUpdate("create unique index idx_word on word(word asc);")
    stmt.executeUpdate("create index idx_word_sorted on word(word_sorted asc);")
    connection.commit
    connection.close

    println("created database")
  }

  def main(args: Array[String]) {
    create
  }

}