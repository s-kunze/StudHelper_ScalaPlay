package models

import org.squeryl.dsl._
import org.squeryl.KeyedEntity
import org.squeryl._

class ModulToLecture(val modulId: Long, val lectureId: Long) extends KeyedEntity[CompositeKey2[Long,Long]] {
  def id = CompositeKey2(modulId, lectureId)
}