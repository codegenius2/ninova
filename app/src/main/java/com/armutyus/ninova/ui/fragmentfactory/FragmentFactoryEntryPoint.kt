package com.armutyus.ninova.ui.fragmentfactory

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface NinovaFragmentFactoryEntryPoint {
    fun getFragmentFactory(): NinovaFragmentFactory
}