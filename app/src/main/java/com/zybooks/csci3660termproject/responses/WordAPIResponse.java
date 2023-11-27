package com.zybooks.csci3660termproject.responses;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
{
  "query": {
    "limit": "100",
    "page": "1"
  },
  "results": {
    "total": 16197,
    "data": [
      "aachen",
      "aargau",
      <...>
      "achira"
    ]
  }
}


*/
public class WordAPIResponse {
    private QueryObj query;

    @SerializedName("results")
    private ResultsObj results;

    public QueryObj getQuery() {
        return query;
    }

    public ResultsObj getResults() {
        return results;
    }

    public static class QueryObj {
        @SerializedName("limit")
        private int limit;
        @SerializedName("page")
        private int page;

        public int getLimit() {
            return limit;
        }

        public int getPage() {
            return page;
        }
    }

    public static class ResultsObj {
        @SerializedName("data")
        private List<String> data;

        @SerializedName("total")
        private int total;

        public List<String> getData() {
            return data;
        }

        public int getTotal() {
            return total;
        }
    }
}
