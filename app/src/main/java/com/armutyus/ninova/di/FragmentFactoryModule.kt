package com.armutyus.ninova.di

import androidx.fragment.app.Fragment
import com.armutyus.ninova.ui.books.BooksFragment
import com.armutyus.ninova.ui.discover.DiscoverFragment
import com.armutyus.ninova.ui.search.MainSearchFragment
import com.armutyus.ninova.ui.search.SearchApiFragment
import com.armutyus.ninova.ui.search.SearchArchiveFragment
import com.armutyus.ninova.ui.settings.SettingsFragment
import com.armutyus.ninova.ui.shelves.ShelvesFragment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class FragmentFactoryModule {

    @Binds
    @IntoMap
    @FragmentKey(BooksFragment::class)
    abstract fun bindBooksFragment(fragment: BooksFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(DiscoverFragment::class)
    abstract fun bindDiscoverFragment(fragment: DiscoverFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(ShelvesFragment::class)
    abstract fun bindShelvesFragment(fragment: ShelvesFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(MainSearchFragment::class)
    abstract fun bindMainSearchFragment(fragment: MainSearchFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(SettingsFragment::class)
    abstract fun bindSettingsFragment(fragment: SettingsFragment): Fragment
    
    @FragmentKey(SearchArchiveFragment::class)
    abstract fun bindSearchArchiveFragment(fragment: SearchArchiveFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(SearchApiFragment::class)
    abstract fun bindSearchApiFragment(fragment: SearchApiFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(SettingsFragment::class)
    abstract fun bindSettingsFragment(fragment: SettingsFragment): Fragment

}