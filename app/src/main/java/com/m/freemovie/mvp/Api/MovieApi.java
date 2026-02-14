package com.m.freemovie.mvp.Api;

import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDetailBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheDownloadBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.AnimePaheSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.DetailBean;
import com.m.freemovie.mvp.Model.ClassBean.DetailDownloadBean;
import com.m.freemovie.mvp.Model.ClassBean.DetailTvBean;
import com.m.freemovie.mvp.Model.ClassBean.DownloadNineAnimeBean;
import com.m.freemovie.mvp.Model.ClassBean.MovieBean;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeBean;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.NineAnimeSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.OtherBean;
import com.m.freemovie.mvp.Model.ClassBean.OthersDlBean;
import com.m.freemovie.mvp.Model.ClassBean.PaheLatestBean;
import com.m.freemovie.mvp.Model.ClassBean.PinoyMovieRuBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.RevivalSeriesBean;
import com.m.freemovie.mvp.Model.ClassBean.SearchRuBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogEpisodeBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogInfoBean;
import com.m.freemovie.mvp.Model.ClassBean.TagalogSearchBean;
import com.m.freemovie.mvp.Model.ClassBean.TvSeriesBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApi {
    @GET("movie/popular")
    Observable<MovieBean> getPopularList(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);

    @GET("movie/top_rated")
    Observable<MovieBean> getTopRated(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);


    @GET("movie/upcoming")
    Observable<MovieBean> getUpcoming(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);



    @GET("movie/now_playing")
    Observable<MovieBean> getNow(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);


    //tv series
    @GET("tv/airing_today")
    Observable<TvSeriesBean> getTodayTv(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);

    @GET("tv/on_the_air")
    Observable<TvSeriesBean> getOnAiringTv(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);


    @GET("tv/popular")
    Observable<TvSeriesBean> getPopularTv(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);



    @GET("tv/top_rated")
    Observable<TvSeriesBean> getTopRatedTv(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("page") int page);




    @GET("tv/{series_id}")
    Observable<DetailTvBean> geTvDetails(
            @Path("series_id") String id,
            @Query("language") String language,
            @Header("Authorization") String authHeader);


    @GET("movie/{id}")
    Observable<DetailBean> getDetails(
            @Path("id") String id,
            @Query("language") String language,
            @Header("Authorization") String authHeader);


    @GET("search/movie")
    Observable<MovieBean> getSearchList(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("query") String query,
            @Query("page") int page,
            @Query("include_adult") boolean includeAdult);

    @GET("search/tv")
    Observable<MovieBean> getSearchSeries(
            @Query("language") String language,
            @Header("Authorization") String authHeader,
            @Query("query") String query,
            @Query("page") int page,
            @Query("include_adult") boolean includeAdult);


    // tagalog dub
    @GET("taganime/series")
    Observable<TagalogBean> getTagalogSeries();

    @GET("taganime/video")
    Observable<TagalogEpisodeBean> getTagalogEpisode(
            @Query("url") String url);

    @GET("taganime")
    Observable<TagalogSearchBean> getSearch(
            @Query("search") String search);


    //revival
    @GET("animerevival/tvshows")
    Observable<RevivalSeriesBean> getRevivalSeries(
            @Query("page") int page);

    @GET("animerevival/movies")
    Observable<RevivalSeriesBean> getRevivalMovies(
            @Query("page") int page);

    @GET("animerevival/info")
    Observable<TagalogInfoBean> getRevivalInfo(
            @Query("url") String url);


    @GET("animerevival/download")
    Observable<DetailDownloadBean> getVideoTrack(
            @Query("url") String url);


    @GET("animerevival")
    Observable<RevivalSearchBean> getSearchRevival(
            @Query("search") String search);


    //9anime
    @GET("9anime/series/ongoing")
    Observable<NineAnimeBean> getNineOngoing(
            @Query("page") int page);

    @GET("9anime/series/latest")
    Observable<NineAnimeBean> getNineLatest(
            @Query("page") int page);

    @GET("9anime/info")
    Observable<NineAnimeEpisodeBean> getNineDetails(
            @Query("url") String url);

    @GET("9anime/download")
    Observable<DownloadNineAnimeBean> getNineEpisodeVideo(
            @Query("url") String url);

    @GET("9anime")
    Observable<NineAnimeSearchBean> getSearchNineAnime(
            @Query("search") String search);


    //animepahe
    @GET("animepahe/latest_release")
    Observable<PaheLatestBean> getLatestAnimePahe(
            @Query("page") int page);

    @GET("animepahe/info")
    Observable<AnimePaheDetailBean> getInfoPahe(
            @Query("url") String url);

    @GET("animepahe/episodes")
    Observable<AnimePaheEpisodeBean> getPaheEpisode(
            @Query("id") String id,
            @Query("page") int page);


    @GET("animepahe/download")
    Observable<AnimePaheDownloadBean> getPaheTrack(
            @Query("url") String url);

    @GET("animepahe/api")
    Observable<AnimePaheSearchBean> getPaheSearch(
            @Query("search") String search);


    //other's api
    @GET("/khflix/genre/sci-fi-fantasy")
    Observable<OtherBean> getSciFiFantasy(@Query("page") int page);

    @GET("/khflix/genre/thai-drama")
    Observable<OtherBean> getThaiDrama(@Query("page") int page);

    @GET("/khflix/genre/crime")
    Observable<OtherBean> getCrime(@Query("page") int page);

    @GET("/khflix/genre/romance")
    Observable<OtherBean> getRomance(@Query("page") int page);

    @GET("/khflix/genre/history")
    Observable<OtherBean> getHistory(@Query("page") int page);

    @GET("/khflix/genre/war")
    Observable<OtherBean> getWar(@Query("page") int page);

    @GET("/khflix/genre/action")
    Observable<OtherBean> getAction(@Query("page") int page);

    @GET("/khflix/genre/drama")
    Observable<OtherBean> getDrama(@Query("page") int page);

    @GET("/khflix/genre/movie-speak-khmer")
    Observable<OtherBean> getMovieSpeakKhmer(@Query("page") int page);

    @GET("/khflix/genre/thriller")
    Observable<OtherBean> getThriller(@Query("page") int page);

    @GET("/khflix/genre/fantasy")
    Observable<OtherBean> getFantasy(@Query("page") int page);

    @GET("/khflix/genre/music")
    Observable<OtherBean> getMusic(@Query("page") int page);

    @GET("/khflix/genre/war-politics")
    Observable<OtherBean> getWarPolitics(@Query("page") int page);

    @GET("/khflix/genre/vivamax")
    Observable<OtherBean> getVivamax(@Query("page") int page);

    @GET("/khflix/genre/tv-movie")
    Observable<OtherBean> getTvMovie(@Query("page") int page);

    @GET("/khflix/genre/documentary")
    Observable<OtherBean> getDocumentary(@Query("page") int page);

    @GET("/khflix/genre/korea-drama")
    Observable<OtherBean> getKoreaDrama(@Query("page") int page);

    @GET("/khflix/genre/mystery")
    Observable<OtherBean> getMystery(@Query("page") int page);

    @GET("/khflix/genre/adventure")
    Observable<OtherBean> getAdventure(@Query("page") int page);

    @GET("/khflix/genre/comedy")
    Observable<OtherBean> getComedy(@Query("page") int page);

    @GET("/khflix/genre/chinese-drama")
    Observable<OtherBean> getChineseDrama(@Query("page") int page);

    @GET("/khflix/genre/science-fiction")
    Observable<OtherBean> getScienceFiction(@Query("page") int page);

    @GET("/khflix/genre/family")
    Observable<OtherBean> getFamily(@Query("page") int page);

    @GET("/khflix/genre/tvshows")
    Observable<OtherBean> getTvShows(@Query("page") int page);

    @GET("/khflix/genre/erotic")
    Observable<OtherBean> getErotic(@Query("page") int page);


    @GET("/khflix/genre/movie")
    Observable<OtherBean> getMovie(@Query("page") int page);


    @GET("/khflix/genre/animation")
    Observable<OtherBean> getAnimation(@Query("page") int page);


    @GET("/khflix/genre/horror")
    Observable<OtherBean> getHorror(@Query("page") int page);


    @GET("/khflix/movies")
    Observable<OtherBean> getAllMovies(@Query("page") int page);


    @GET("/khflix/download")
    Observable<OthersDlBean> getOtherDownload(@Query("url") String url);


    //tagalog movie

    @GET("wp/v2/movies")
    Observable<List<PinoyMovieRuBean>> getPiNoyRuPage(@Query("page") int page);

    @GET("wp/v2/search")
    Observable<List<SearchRuBean>> getSearchRu(@Query("search") String search);

}