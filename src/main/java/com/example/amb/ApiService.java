package com.example.amb; // Ensure this matches your package name

import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Assuming this endpoint is for submitting some alert data
    // The response type Map<String, Object> is generic; replace with a specific POJO if you have one
    @POST("alerts/submit/") // Make sure this endpoint is correct
    Call<Map<String, Object>> submitAlert(@Body Map<String, String> body); // <<< --- ADDED SEMICOLON HERE

    // Endpoint to get hospital details by code
    @GET("hospitals/{hospital_code}/") // Make sure this endpoint is correct
    Call<HospitalDetailsResponse> getHospitalDetails(@Path("hospital_code") String hospitalCode);

    /**
     * Fetches a route from the OSRM API.
     * Note: The @GET annotation here uses a full URL because OSRM is an external service
     * and might have a different base URL than your primary API.
     *
     * @param profile     The routing profile (e.g., "driving", "walking", "cycling").
     * @param coordinates The string of coordinates in "lon1,lat1;lon2,lat2" format.
     * @param overview    Controls the level of detail for the route geometry ("full", "simplified", "false").
     * @param geometries  The format of the route geometry ("polyline" for precision 5).
     * @param alternatives Set to false to get only the primary route.
     * @param steps       Set to false if you don'tneed turn-by-turn steps.
     * @return A Call object for the OSRM API, returning ResponseBody to handle raw JSON.
     */
    @GET("http://router.project-osrm.org/route/v1/{profile}/{coordinates}")
    Call<ResponseBody> getRoute(
            @Path("profile") String profile,
            @Path("coordinates") String coordinates,
            @Query("overview") String overview,
            @Query("geometries") String geometries,
            @Query("alternatives") boolean alternatives,
            @Query("steps") boolean steps
    );

    // If you have other API calls, define them here// For example, if you had an endpoint to get a list of all hospitals:
    // @GET("hospitals/")
    // Call<List<HospitalSummaryResponse>> getAllHospitals();
}