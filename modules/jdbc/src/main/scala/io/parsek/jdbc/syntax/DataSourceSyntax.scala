package io.parsek.jdbc.syntax

import javax.sql.DataSource

import io.parsek.jdbc.{JdbcQueryExecutor, NameConverter, QueryExecutor}
import resource._

import scala.language.implicitConversions

/**
  * @author Andrei Tupitcyn
  */

trait DataSourceSyntax {
  final implicit def dataSourceSyntaxOps(dataSource: DataSource): DataSourceOps = new DataSourceOps(dataSource)
}

final class DataSourceOps(val dataSource: DataSource) extends AnyVal {
  def withQueryExecutor[A](f: QueryExecutor => A): A = {
    val res = for {
      connection <- managed(dataSource.getConnection)
    } yield {
      val qe = JdbcQueryExecutor(connection)
      f(qe)
    }
    res.acquireAndGet(identity)
  }

  def withQueryExecutor[A](nameConverter: NameConverter)(f: QueryExecutor => A): A = {
    val res = for {
      connection <- managed(dataSource.getConnection)
    } yield {
      val qe = JdbcQueryExecutor(connection, nameConverter)
      f(qe)
    }
    res.acquireAndGet(identity)
  }
}
