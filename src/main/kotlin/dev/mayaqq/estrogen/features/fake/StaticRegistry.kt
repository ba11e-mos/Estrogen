package dev.mayaqq.estrogen.features.fake

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Lifecycle
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderOwner
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import uwu.serenity.kritter.stdlib.location
import java.util.Optional
import java.util.stream.Stream

class StaticRegistry<T>(
    val registry: ResourceKey<out Registry<T>>,
    val valueKey: ResourceKey<T>,
    val value: T
) : Registry<T> {
    override fun key(): ResourceKey<out Registry<T>> = this.registry
    override fun getKey(value: T): ResourceLocation = this.valueKey.location
    override fun getResourceKey(value: T): Optional<ResourceKey<T>> = Optional.of(this.valueKey)
    override fun getId(value: T?): Int = 0
    override fun get(key: ResourceKey<T>?): T? = value
    override fun get(location: ResourceLocation?): T? = value
    override fun lifecycle(value: T): Lifecycle = Lifecycle.stable()
    override fun registryLifecycle(): Lifecycle = Lifecycle.stable()
    override fun keySet(): Set<ResourceLocation> = setOf(this.valueKey.location)
    override fun entrySet(): Set<Map.Entry<ResourceKey<T>, T>> = setOf(mapOf(this.valueKey to this.value).entries.first())
    override fun registryKeySet(): Set<ResourceKey<T>> = setOf(this.valueKey)
    override fun getRandom(source: RandomSource): Optional<Holder.Reference<T>> = Optional.empty()
    override fun containsKey(location: ResourceLocation): Boolean = true
    override fun containsKey(key: ResourceKey<T>): Boolean = true
    override fun freeze(): Registry<T> = this
    override fun createIntrusiveHolder(holder: T): Holder.Reference<T> = throw UnsupportedOperationException()
    override fun getHolder(index: Int): Optional<Holder.Reference<T>> = Optional.empty()
    override fun getHolder(key: ResourceKey<T>): Optional<Holder.Reference<T>> = Optional.empty()
    override fun wrapAsHolder(value: T): Holder<T> = throw UnsupportedOperationException()
    override fun holders(): Stream<Holder.Reference<T>> = listOf<Holder.Reference<T>>().stream()
    override fun getTag(p0: TagKey<T?>): Optional<HolderSet.Named<T>> = Optional.empty()
    override fun getOrCreateTag(p0: TagKey<T?>): HolderSet.Named<T?> = throw UnsupportedOperationException()
    override fun getTags(): Stream<Pair<TagKey<T?>?, HolderSet.Named<T?>?>?> = throw UnsupportedOperationException()
    override fun getTagNames(): Stream<TagKey<T?>?> = throw UnsupportedOperationException()
    override fun resetTags() {}
    override fun bindTags(p0: Map<TagKey<T?>?, List<Holder<T?>?>?>) {}
    override fun holderOwner(): HolderOwner<T?> = throw UnsupportedOperationException()
    override fun asLookup(): HolderLookup.RegistryLookup<T?> = throw UnsupportedOperationException()
    override fun byId(index: Int): T = value
    override fun size(): Int = 1
    override fun iterator(): MutableIterator<T> = Stream.of(this.value).iterator()

    class Frozen<T>(val registry: StaticRegistry<T>) : RegistryAccess.Frozen {
        override fun <E> registry(key: ResourceKey<out Registry<out E>>): Optional<Registry<E>> {
            return Optional.of(registry as Registry<E>)
        }

        override fun registries(): Stream<RegistryAccess.RegistryEntry<*>?> {
            return Stream.of(RegistryAccess.RegistryEntry(registry.registry, registry))
        }
    }
}