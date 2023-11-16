package com.zybooks.csci3660termproject.responses;
import com.google.gson.annotations.SerializedName;

public class WordAPIResponse {
    @SerializedName("query")
    private Query query;

    @SerializedName("results")
    private Results results;

    public Query getQuery() {
        return query;
    }

    public Results getResults() {
        return results;
    }

    public static class Query {
        @SerializedName("limit")
        private String limit;

        @SerializedName("page")
        private String page;

        public String getLimit() {
            return limit;
        }

        public String getPage() {
            return page;
        }
    }

    public static class Results {
        @SerializedName("total")
        private int total;

        @SerializedName("data")
        private String[] data;

        public int getTotal() {
            return total;
        }

        public String[] getData() {
            return data;
        }
    }
}
