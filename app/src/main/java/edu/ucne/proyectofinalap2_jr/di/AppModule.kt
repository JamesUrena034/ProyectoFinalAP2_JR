package edu.ucne.proyectofinalap2_jr.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.proyectofinalap2_jr.data.repository.CategoriaRepositoryImpl
import edu.ucne.proyectofinalap2_jr.data.repository.PedidoRepositoryImpl
import edu.ucne.proyectofinalap2_jr.data.repository.ProductoRepositoryImpl
import edu.ucne.proyectofinalap2_jr.domain.repository.CategoriaRepository
import edu.ucne.proyectofinalap2_jr.domain.repository.PedidoRepository
import edu.ucne.proyectofinalap2_jr.domain.repository.ProductoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideProductoRepository(
        firestore: FirebaseFirestore
    ): ProductoRepository = ProductoRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideCategoriaRepository(
        firestore: FirebaseFirestore
    ): CategoriaRepository = CategoriaRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun providePedidoRepository(
        firestore: FirebaseFirestore
    ): PedidoRepository = PedidoRepositoryImpl(firestore)
}