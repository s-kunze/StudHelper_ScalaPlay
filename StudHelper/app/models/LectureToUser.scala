package models

import org.squeryl.dsl._
import org.squeryl.KeyedEntity
import org.squeryl._

class LectureToUser(val lectureId: Long, val userId: Long, var mark: Float) extends KeyedEntity[CompositeKey2[Long,Long]] {
  def id = CompositeKey2(lectureId, userId)
}