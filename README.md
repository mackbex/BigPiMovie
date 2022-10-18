# BigPiMovie 아키텍처 설명도

> <p> - 사용 IDE : Android Studio 2021.2.1 (Chipmunk)  <p/>
> <p> - 호환성 : API24+ <p/>
> <p> - 언어 : Kotlin <p/>
> <p> - Architecture : Clean Architecture에 기반한 MVVM (module) <p/>
> <p> - DI : Hilt <p/>
> <p> - 이미지처리 : Glide <p/>
> <p> - API 네트워크 라이브러리 : Retrofit2 및 코루틴 <p/>
> <p> - Local : Room 및 코루틴 <p/>
> <p> - UI : ConstraintLayout <p/>
> <p> - 기타 : NavigationComponent, Databinding, Stateflow 사용, AAC, Jetpack, Material, 버전관리를 위한 Gradle toml사용<p/>

#### 아키텍처

> 관심사 분리, 종속성 해결을 위해 클린 아키텍처를 지향했습니다. <br/>
> 각각의 모듈별로 구현했으며,<br/>
> - **App(Present)**<br/>
> - **Data(Android Library)**<br/>
> - **Domain(Kotlin Library)**<br/>
>
> 로 구현했습니다.<br/>
>
><img src="https://user-images.githubusercontent.com/5334962/196419714-ffbfed7a-4e42-4098-97f2-d8e69acc85d0.png" width="800" />
>

#### Presenter

> ***프리젠터 영역***은 그림과 같이 2개의 Fragment가 있습니다. NavigationComponent로 구성했기 때문에, 단일 액티비티를 지향하고 있고, 
> - ViewModel에서 Domain 레이어의 useCase를 통해 api를 호출합니다. (실제 구현 및 실행은 Data layer에서 진행됨.)
> - Resource라는 sealed 클래스(성공, 실패, 로딩 상태 에 따른 conditional result.)로 결과 값을 감싸 StateFlow으로 전달합니다.
> - StateFlow와 DataBinding을 통해 화면을 구성합니다.
> - 도메인과 데이터, 프레젠터 영역들간의 model 변환을 위한 mapper도 모듈에 포함되어 있습니다.
> - 만약 네트워크나 기타 사유로 데이터 호출이 실패하는 경우, 
> <img src="https://user-images.githubusercontent.com/5334962/196431979-0dcd287d-bceb-49b2-b387-0f3577a289c8.png" width="300" /> <br/>
> 위와같이 실패 ui가 최하단에 표기되며, retry를 누를 시 마지막 성공한 영화 리스트부터 fetch를 진행합니다.

#### Domain

> ***도메인 영역***은 MovieRepository(Interface)와 SearchMovieUseCase, UpdateBookmarkUseCase가 있습니다.<br/>
> - MovieRepository의 실제 구현부(MovieRepositoryImpl)는 Data 모듈에 있습니다. <br/>
> - SearchMovieUseCase, UpdateBookmarkUseCase는 실제 유저가 어떤 사용을 하는지에 대한 비즈니스 로직을 담았습니다.<br/>
> - 도메인과 데이터, 프레젠터 영역들간의 model 변환을 위한 mapper도 모듈에 포함되어 있습니다.

#### Data

> ***데이터 영역***은 영화 검색결과를 지속적으로 fetch하기 위한 **remote(Retrofit2)영역**, 북마크 상태 저장 및 업데이트를 위한 **local(Room) 영역**이 있습니다. <br/>
> 각각 repository or dao의 결과를 담당하는 Resource라는 이름의 sealed 클래스 wrapper를 통해,
> - 초기값은 Resource.Loading,
> - 성공시 Resource.Success, 
> - 실패시 Resource.Failure
> 를 리턴합니다. 
> - 도메인과 데이터, 프레젠터 영역들간의 model 변환을 위한 mapper도 모듈에 포함되어 있습니다.
>
#### Test
> 우대사항에 테스트 코드의 작성 여부가 있어, 간단하게나마 given, when, then 패을 사용한 비즈니스 로직 테스트를 2가지 구현했습니다. <br/>
>
> 실 업무시에 mock 데이터 구현시 mockito / mockk를 주로 사용하지만, MVVM의 가장 큰 장점 중 하나인 테스팅의 용이성을 최대한 library없이 적용하고 싶기에, repository 구현부 작성 아래 진행했습니다.
> - FetchMovieTest : paging 로직의 가장 중요한 부분인, 추가 페이지의 여부를 판단하는 테스트를 기술했습니다. 제공된 api를 통해 추가 페이지가 있는지에 대한 여부는 (total - (display + start) > 0) 을 기준으로 진행했습니다.
> - BookmarkMovieTest : 북마크는 어떻게보면 value = !value라는 판단 하에, boolean 값을 usecase에 넣으면 반대값이 정상적으로 나오는지만 테스트했습니다.

#### 추가 사항
> - 우대사항을 최대한 반영하려 노력했습니다. 만약 우대사항의 한가지인 flow 활용 여부가 데이터 홀더의 경우를 의도하신거라면, 요건에 맞게 작업했다 말씀드리고 싶습니다.
> - branch 내역들을 보면 확인하실 수 있겠지만, Paging3 적용을 실험적으로 진행했으나,
>     1. 역순으로 스크롤 시, 포지션이 이상한 곳을 향함,
>     2. paging3 실제 데이터 접근은 snapshot을 통해 현 상태를 바꾼다 해도, flow에서 내려받는 스트림 자체 수정은 라이브러리에서 지원하지 않기에, 데이터 업데이트시 api 호출을 다시해야함.)
>     3. api 재호출을 피하기 위해 RemoteMediator를 도입했으나 역순으로 스크롤 시에 예상못한 ui 업데이트 애니메이션이 보임.<br/>
>
> 등의 이유로, 결론적으로 하나의 아이템 상태를 update하는건 기술적으로 어렵다  판단하여 infinite scroll 방식으로 구현했습니다.
> - Gradle의 버전관리를 위해 실험적으로 toml을 넣었습니다. 기존 ext 사용과의 차이점은, plugin, classpath 등 다양한 영역의 변수들도 버전을 포함해 함께 관리할 수 있고, 같은 패키지면 bundle로 묶어주는 기능도 제공하는 편리함 때문에 사용했습니다. (/gradle/lib/versions.toml 참조)

#### 프로파일링
><img src="https://user-images.githubusercontent.com/5334962/196428245-ace71255-b9bc-40d2-b8ca-f27f47325337.png" width="400" /> <br/>
><img src="https://user-images.githubusercontent.com/5334962/196428902-70ccf3f1-b00b-4f6d-a08e-2d8c85acd0cb.png" width="400" /> <br/>
> - OS : 안드로이드 12 버전(갤럭시 S21+ 기준)
> - CPU : fetch 진행시 약 20~30%
> - 메모리 : 약 200~220mb
> - 베터리 상태 : 양호<br/>
>
> layout inspector를 통해, 초기 실행 cpu, 메모리 점유 후 UI는 정상적으로 recycling이 진행되는점 확인했습니다. 다만, 실제 fetch를 진행할 수록 누적되는 데이터 list 자체는 listAdapter에서 인메모리에 점진적으로 쌓이기 때문에, (약 fetch 1회에 500kb 내외라 생각합니다.) 이부분을 좀더 개선할 수 있는 방법이 있으면 좀더 좋겠다 생각했습니다.

