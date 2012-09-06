package models.db

import in.partake.app.PartakeApp
import in.partake.base.PartakeException
import in.partake.model.IPartakeDAOs
import in.partake.model.dao.DAOException
import in.partake.model.dao.PartakeConnection
import in.partake.service.IDBService

object Transaction {
  def apply[T](f: (PartakeConnection, IPartakeDAOs) => T): T = {
    val service: IDBService = PartakeApp.getDBService()

    val con: PartakeConnection = service.getConnection()
    try {
      con.beginTransaction();
      val result: T = f(con, service.getDAOs())

      if (con.isInTransaction())
        con.commit()

      return result
    } finally {
      try {
        if (con.isInTransaction())
          con.rollback()
      } finally {
          con.invalidate()
      }
    }
  }
}




