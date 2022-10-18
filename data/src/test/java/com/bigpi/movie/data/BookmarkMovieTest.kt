package com.bigpi.movie.data

import com.bigpi.movie.data.factory.MovieFactory
import com.bigpi.movie.data.model.mapper.mapToDomain
import com.bigpi.movie.data.repository.FakeMovieRepository
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.usecase.SearchMovieUseCase
import com.bigpi.movie.domain.usecase.UpdateBookmarkUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

class BookmarkMovieTest {


    @Test
    fun `북마크 작동 여부 테스트`() = runBlocking {

        /**
         * 북마크가 false일 경우, true로 변환한다.
         */
        //given
        var given = MovieFactory().getMovieItemResponse(false).mapToDomain()
        //when
        var movie = UpdateBookmarkUseCase(FakeMovieRepository(1)).invoke(given).let { (it as Resource.Success).data }
        //then :
        assertEquals(true, movie.bookmark)

        /**
         * 북마크가 true일 경우, false로 변환한다.
         */
        //given
        given = MovieFactory().getMovieItemResponse(true).mapToDomain()
        //when
        movie = UpdateBookmarkUseCase(FakeMovieRepository(1)).invoke(given).let { (it as Resource.Success).data }
        //then :
        assertEquals(false, movie.bookmark)
    }
}