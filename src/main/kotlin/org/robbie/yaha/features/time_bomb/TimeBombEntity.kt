package org.robbie.yaha.features.time_bomb

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.getBoolean
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.api.utils.hasByte
import at.petrak.hexcasting.api.utils.hasCompound
import at.petrak.hexcasting.api.utils.hasInt
import at.petrak.hexcasting.api.utils.hasList
import at.petrak.hexcasting.api.utils.hasLong
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.robbie.yaha.YahaUtils
import org.robbie.yaha.registry.YahaEntities
import org.robbie.yaha.registry.YahaItems

const val DRAG = 0.9

class TimeBombEntity(
    entityType: EntityType<out TimeBombEntity>,
    world: World
) : ThrownItemEntity(entityType, world) {
    constructor(
        world: World,
        owner: Entity?,
        hex: List<Iota>,
        media: Long,
        pigment: FrozenPigment,
        lifetime: Int,
        pos: Vec3d
    ) : this(YahaEntities.TIME_BOMB_ENTITY, world) {
        this.owner = owner
        this.hex = hex
        this.media = media
        this.pigment = pigment
        this.lifetime = lifetime
        setPosition(pos)
    }

    private var hex: List<Iota> = listOf()
    private var media: Long = 0
    var pigment = FrozenPigment.DEFAULT.get()
    private var lifetime = 0

    fun getMedia() = media
    fun setMedia(value: Long) {
        media = value.coerceAtLeast(0)
    }

    override fun tick() {
        super.tick()
        if (age >= lifetime) explode()

        // check collision
        val hitResult = ProjectileUtil.getCollision(this, ::canHit)
        if (hitResult.type != HitResult.Type.MISS) onCollision(hitResult)

        // update position, rotation, and velocity
        YahaUtils.pitchYawFromRotVec(velocity)?.let {
            pitch = it.first
            yaw = it.second
        }
        setPosition(pos.add(velocity))
        velocity = velocity.multiply(DRAG)

        checkBlockCollision() // other minecraft projectiles seem to call both onCollision AND checkBlockCollision
    }

    private fun explode() {
        if (world !is ServerWorld) return

        if (hex.isNotEmpty()) {
            val env = TimeBombCastEnv(world as ServerWorld, this)

            var castingImage = CastingImage()
            val castingVM = CastingVM(castingImage, env)
            val clientView = castingVM.queueExecuteAndWrapIotas(hex, world as ServerWorld)
        }

        playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 0.5f)
        world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
        discard()
    }

    override fun handleStatus(status: Byte) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            val particleParam = ItemStackParticleEffect(
                ParticleTypes.ITEM,
                ItemStack(Items.AMETHYST_BLOCK, 1)
            )

            for (i in 0..7)
                world.addParticle(
                    particleParam,
                    x,
                    y,
                    z,
                    world.random.nextDouble() - 0.5,
                    world.random.nextDouble() - 0.5,
                    world.random.nextDouble() - 0.5
                )
        }
    }

    override fun canHit(entity: Entity?) = false
    override fun canHit() = true

    override fun damage(source: DamageSource?, amount: Float): Boolean {
        if (isInvulnerableTo(source)) return false
        val entity = source?.attacker
        if (entity == null) return false

        if (!world.isClient) {
            velocity = entity.rotationVector.multiply(0.4)
        }

        return true
    }

    override fun onBlockHit(blockHitResult: BlockHitResult?) {
        if (blockHitResult == null) return
        val scale = blockHitResult.side.vector.multiply(-2).add(1, 1, 1)
        velocity = velocity.multiply(scale.x.toDouble(), scale.y.toDouble(), scale.z.toDouble())
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {
        super.writeCustomDataToNbt(nbt)
        nbt ?: return

        if (hex.isNotEmpty()) {
            val hexNbt = NbtList()
            hex.forEach { hexNbt.add(IotaType.serialize(it)) }
            nbt.putList("hex", hexNbt)
        }
        nbt.putLong("media", media)
        nbt.putCompound("pigment", pigment.serializeToNBT())
        nbt.putInt("lifetime", lifetime)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
        super.readCustomDataFromNbt(nbt)

        hex = if (nbt?.hasList("hex") == true && world is ServerWorld) {
            val hexNbt = nbt.getList("hex", NbtElement.COMPOUND_TYPE)
            hexNbt.map { IotaType.deserialize(it.asCompound, world as ServerWorld) }
        } else listOf()

        media = if (nbt?.hasLong("media") == true)
            nbt.getLong("media")
        else 0

        pigment = if (nbt?.hasCompound("pigment") == true)
            FrozenPigment.fromNBT(nbt.getCompound("pigment"))
        else FrozenPigment.DEFAULT.get()

        lifetime = if (nbt?.hasInt("lifetime") == true)
            nbt.getInt("lifetime")
        else 0
    }

    override fun getDefaultItem() = YahaItems.TIME_BOMB_ITEM

    override fun hasNoGravity() = true
}