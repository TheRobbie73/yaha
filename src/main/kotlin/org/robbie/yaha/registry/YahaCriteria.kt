package org.robbie.yaha.registry

import com.google.gson.JsonObject
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import org.robbie.yaha.Yaha

object YahaCriteria {
    val COLLIDE_PLANES = CollidePlanesCriterion()
    val SUSCEPTION = SusceptionCriterion()
    val BOMB_DEFUSAL = BombDefusalCriterion()

    fun register() {
        Criteria.register(COLLIDE_PLANES)
        Criteria.register(SUSCEPTION)
        Criteria.register(BOMB_DEFUSAL)
    }
}

class CollidePlanesCriterion : AbstractCriterion<CollidePlanesCriterion.Condition>() {
    override fun conditionsFromJson(
        obj: JsonObject?,
        playerPredicate: LootContextPredicate?,
        predicateDeserializer: AdvancementEntityPredicateDeserializer?
    ) = Condition()

    override fun getId() = ID

    fun trigger(player: ServerPlayerEntity) = trigger(player) { true }

    class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
    companion object { val ID = Yaha.id("collide_planes") }
}

class SusceptionCriterion : AbstractCriterion<SusceptionCriterion.Condition>() {
    override fun conditionsFromJson(
        obj: JsonObject?,
        playerPredicate: LootContextPredicate?,
        predicateDeserializer: AdvancementEntityPredicateDeserializer?
    ) = Condition()

    override fun getId() = ID

    fun trigger(player: ServerPlayerEntity) = trigger(player) { true }

    class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
    companion object { val ID = Yaha.id("susception") }
}

class BombDefusalCriterion : AbstractCriterion<BombDefusalCriterion.Condition>() {
    override fun conditionsFromJson(
        obj: JsonObject?,
        playerPredicate: LootContextPredicate?,
        predicateDeserializer: AdvancementEntityPredicateDeserializer?
    ) = Condition()

    override fun getId() = ID

    fun trigger(player: ServerPlayerEntity) = trigger(player) { true }

    class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
    companion object { val ID = Yaha.id("bomb_defusal") }
}