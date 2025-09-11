package com.ariabagas.storiaapp.ui.home

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.ariabagas.storiaapp.DataDummy
import com.ariabagas.storiaapp.MainDispatcherRule
import com.ariabagas.storiaapp.core.model.Story
import com.ariabagas.storiaapp.core.usecase.HomeUseCase
import com.ariabagas.storiaapp.getOrAwaitValue
import com.ariabagas.storiaapp.ui.home.StoryAdapter.Companion.DIFF_CALLBACK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var homeUseCase: HomeUseCase
    private lateinit var mockedLog: MockedStatic<Log>

    @Before
    fun setUp() {
        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedLog.`when`<Boolean> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }
            .thenReturn(false)
        mockedLog.`when`<Int> { Log.d(Mockito.anyString(), Mockito.anyString()) }
            .thenReturn(0)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStories()
        val data: PagingData<Story> = PagingData.from(dummyStories)
        val expectedStories = flowOf(data)

        Mockito.`when`(homeUseCase.getStories("token")).thenReturn(expectedStories)

        val viewModel = HomeViewModel(homeUseCase)
        val actualStories: PagingData<Story> = viewModel.getStories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)
        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStories = flowOf(data)

        Mockito.`when`(homeUseCase.getStories("token")).thenReturn(expectedStories)

        val viewModel = HomeViewModel(homeUseCase)
        val actualStories: PagingData<Story> = viewModel.getStories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)
        advanceUntilIdle()

        Assert.assertTrue(differ.snapshot().isEmpty())
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
