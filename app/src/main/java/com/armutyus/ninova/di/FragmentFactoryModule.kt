package com.armutyus.ninova.di

import androidx.fragment.app.Fragment
import com.armutyus.ninova.ui.books.BookUserNotesFragment
import com.armutyus.ninova.ui.books.BooksFragment
import com.armutyus.ninova.ui.discover.DiscoverFragment
import com.armutyus.ninova.ui.search.MainSearchFragment
import com.armutyus.ninova.ui.settings.SettingsFragment
import com.armutyus.ninova.ui.shelves.BookToShelfFragment
import com.armutyus.ninova.ui.shelves.ShelfWithBooksFragment
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

    @Binds
    @IntoMap
    @FragmentKey(ShelfWithBooksFragment::class)
    abstract fun bindShelfWithBooksFragment(fragment: ShelfWithBooksFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(BookToShelfFragment::class)
    abstract fun bindBookToShelfFragment(fragment: BookToShelfFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(BookUserNotesFragment::class)
    abstract fun bindBookUserNotesFragment(fragment: BookUserNotesFragment): Fragment
}