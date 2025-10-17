package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Unexpected response");
            }
            return response.body().string();
        }
    }

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {

        List<String> subBreeds = new ArrayList<>();

        try {
            String responseBodyString = this.run("https://dog.ceo/api/breed/" + breed + "/list");
            JSONObject jsonObj = new JSONObject(responseBodyString);

            if (jsonObj.getString("status").equals("error")) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray jsonArray = jsonObj.getJSONArray("message");

            for (int i = 0; i < jsonArray.length(); i++) {
                subBreeds.add(jsonArray.getString(i));
            }
        }
        catch(IOException e) {
            throw new BreedNotFoundException(breed);
        }

        return subBreeds;
    }
}