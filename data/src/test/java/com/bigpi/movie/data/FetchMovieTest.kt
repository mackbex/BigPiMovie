package com.bigpi.movie.data

import com.bigpi.movie.data.repository.FakeMovieRepository
import com.bigpi.movie.domain.Resource
import com.bigpi.movie.domain.usecase.SearchMovieUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

class FetchMovieTest {


    @Test
    fun `추가 로드가 있는지 여부 테스트`() = runBlocking {

        val given = FakeMovieRepository(10)
        /**
         * 전체 페이지가 display 와 같을 경우
         */
        //when
        var movie = SearchMovieUseCase(given).invoke("test",10,1).let { (it as Resource.Success).data }
        //then :
        assertEquals(false, movie.hasMoreLoads)

        /**
         * 전체 페이지가 display 보다 클 경우
         */
        //when
        movie = SearchMovieUseCase(given).invoke("test",5,1).let { (it as Resource.Success).data }
        //then :
        assertEquals(true, movie.hasMoreLoads)

        /**
         * 전체 페이지가 display 보다 작을 경우
         */
        //when
        movie = SearchMovieUseCase(given).invoke("test",12,1).let { (it as Resource.Success).data }
        //then :
        assertEquals(false, movie.hasMoreLoads)

        /**
         * start 위치가 다른 경우
         */
        //when
        movie = SearchMovieUseCase(given).invoke("test",12,10).let { (it as Resource.Success).data }
        //then :
        assertEquals(false, movie.hasMoreLoads) // display가 12이고 스타트가 10이면 전체 페이지가 10이기 때문에 추가 로드는 없다.

        //when
        movie = SearchMovieUseCase(given).invoke("test",5,4).let { (it as Resource.Success).data }
        //then :
        assertEquals(true, movie.hasMoreLoads) // display가 5이고 스타트가 4이면 전체 페이지가 10이기 때문에 추가 로드가 있다.

    }
}