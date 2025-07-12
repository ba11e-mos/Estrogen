package dev.mayaqq.estrogen.client.cosmetics

data class Cosmetic(
    val id: String,
    val name: String,
    val texture: CosmeticTexture,
    val model: CosmeticModel,
    val animation: CosmeticAnimation?
    )
