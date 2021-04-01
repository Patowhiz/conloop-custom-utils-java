package conloop;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Can be universally used by any java app to get or post values using http
 * connection Important to note. All http connection is done in a separate
 * thread from the given ExecutorService Important to note. All results are
 * dispatched through the thread of the given the ExecutorService doing the http
 * request
 *
 * @author patowhiz 30/12/2020 at 03:36 PM in Machakos Office
 */
public class NetworkBridge {

    private static NetworkBridge INSTANCE;
    protected final boolean bUseSecureConnection;
    //used to determine the thread under which the newtork io will be done on, and results dispatched through
    private final ExecutorService executorService;

    public NetworkBridge(boolean bUseSecureConnection, ExecutorService executorService) {
        this.bUseSecureConnection = bUseSecureConnection;
        this.executorService = executorService;
        //Set VM-wide cookie management. This SHOULD BE DONE ONLY ONCE
        CookieHandler.setDefault(new CookieManager());
    }

    // Access it through your singleton class.
    public static NetworkBridge getInstance(boolean bUseSecureConnection) {
        return getInstance(bUseSecureConnection, Executors.newSingleThreadExecutor());
    }

    public static NetworkBridge getInstance(boolean bUseSecureConnection, ExecutorService executorService) {
        if (INSTANCE == null) {
            synchronized (NetworkBridge.class) {
                INSTANCE = new NetworkBridge(bUseSecureConnection, executorService);
            }
        }
        return INSTANCE;
    }

    protected java.net.HttpURLConnection getHttpUrlConn(String urlString) {
        java.net.HttpURLConnection httpUrlConn = null;
        try {
            java.net.URL url = new java.net.URL(urlString);
            httpUrlConn = (java.net.HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setDoOutput(true);
        } catch (java.net.MalformedURLException ex) {
            LoggingUtil.e(NetworkBridge.class, "MalformedURLException in UrlSetUp : " + ex.getMessage());
        } catch (java.io.IOException ex) {
            LoggingUtil.e(NetworkBridge.class, "IOException in setHttpUrlConn : " + ex.getMessage());
        }//end try//end try//end try//end try//end try//end try//end try//end try

        return httpUrlConn;
    }

    protected javax.net.ssl.HttpsURLConnection getHttpsUrlConn(String urlString) {
        javax.net.ssl.HttpsURLConnection httpsUrlConn = null;
        try {
            java.net.URL url = new java.net.URL(urlString);
            httpsUrlConn = (javax.net.ssl.HttpsURLConnection) url.openConnection();
            httpsUrlConn.setDoInput(true);
            httpsUrlConn.setDoOutput(true);
        } catch (java.net.MalformedURLException ex) {
            LoggingUtil.e(NetworkBridge.class, "MalformedURLException in UrlSetUp : " + ex.getMessage());
        } catch (java.io.IOException ex) {
            LoggingUtil.e(NetworkBridge.class, "IOException in setHttpUrlConn : " + ex.getMessage());

        }//end try//end try//end try//end try//end try//end try//end try//end try

        return httpsUrlConn;

    }//end method

    /**
     * reads from the connection and returns a string value
     *
     * @param con
     * @return string
     * @throws IOException
     */
    protected String getRequest(java.net.HttpURLConnection con) throws IOException {
        StringBuilder strBuilder = new StringBuilder("");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                strBuilder.append(line);
            }//end while
        }//end try-with-resource

        return strBuilder.toString();
    }

    protected void getRequest(String url, CustomTaskResult results, Class tClass) {
        executorService.execute(() -> {
            java.net.HttpURLConnection con = bUseSecureConnection ? getHttpsUrlConn(url) : getHttpUrlConn(url);
            try {

                String strResponse = getRequest(con);

                if (StringsUtil.isNullOrEmpty(strResponse)) {
                    results.setException(new CustomTaskException(CustomTaskException.RESPONSE_ERROR, "noresponse"));
                    return;
                }

                //trim off responses. Very importamt incase response comes with prespaces e.g in the case of php
                strResponse = strResponse.trim();
                if (tClass.getSimpleName().equals(String.class.getSimpleName())) {
                    results.setResult(strResponse);
                } else if (tClass.getSimpleName().equals(JSONObject.class.getSimpleName())) {
                    results.setResult(new JSONObject(strResponse));
                } else if (tClass.getSimpleName().equals(JSONArray.class.getSimpleName())) {
                    results.setResult(new JSONArray(strResponse));
                } else {
                    results.setException(new CustomTaskException(CustomTaskException.RESPONSE_ERROR, "uknownresponse"));
                }
            } catch (IOException ioe) {
                LoggingUtil.e(NetworkBridge.class, "IO error response: " + ioe.getMessage());
                results.setException(new CustomTaskException(CustomTaskException.NETWORK_ERROR, handleNetworkErrorResponse(con.getErrorStream())));
            } catch (JSONException jsone) {
                results.setException(new CustomTaskException(CustomTaskException.EXCEPTION_ERROR, "JSON Exception in getting online data: " + jsone.getMessage()));
            } catch (Exception ex) {
                results.setException(new CustomTaskException(CustomTaskException.EXCEPTION_ERROR, "Exception in getting online data: " + ex.getMessage()));
            }//try
        });
    }//end method

    public CustomTask<String> getStringRequest(String url) {
        CustomTaskResult<String> res = new CustomTaskResult<>();
        getRequest(url, res, String.class);
        return res;
    }

    public CustomTask<JSONObject> getJsonObjectRequest(String url) {
        CustomTaskResult<JSONObject> res = new CustomTaskResult<>();
        getRequest(url, res, JSONObject.class);
        return res;
    }

    public CustomTask<JSONArray> getJsonArrayRequest(String url) {
        CustomTaskResult<JSONArray> res = new CustomTaskResult<>();
        getRequest(url, res, JSONArray.class);
        return res;
    }

    public CustomTaskResult<String> postRequest(String url, JSONArray jsonArray) {
        return postRequest(url, jsonArray.toString(), true);
    }

    public CustomTaskResult<String> postRequest(String url, JSONObject jsonobject) {
        return postRequest(url, jsonobject.toString(), true);
    }

    public CustomTaskResult<String> postRequest(String url, String formEncodedValues) {
        return postRequest(url, formEncodedValues, false);
    }

    protected CustomTaskResult<String> postRequest(String url, String urlParamValues, boolean bAsJSON) {
        CustomTaskResult<String> res = new CustomTaskResult<>();
        postRequest(url, urlParamValues, bAsJSON, res);
        return res;
    }

    protected void postRequest(String url, String urlParamValues, boolean bAsJSON, CustomTaskResult results) {
        executorService.execute(() -> {
            java.net.HttpURLConnection con = bUseSecureConnection ? getHttpsUrlConn(url) : getHttpUrlConn(url);
            boolean donePosting = false;
            try {
                con.setRequestMethod("POST");
                con.setInstanceFollowRedirects(false);
                con.setUseCaches(false);
                if (bAsJSON) {
                    con.setRequestProperty("Content-Type", "application/json");
                } else {
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("charset", "utf-8");
                    con.setRequestProperty("Content-Length", Integer.toString(urlParamValues.length()));
                }

                //post first
                try (OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream())) {
                    out.write(urlParamValues);
                    donePosting = true;
                } catch (IOException ex) {
                    results.setException(new CustomTaskException(CustomTaskException.NETWORK_ERROR, "Error in writing to server: " + ex.getMessage()));
                }//end try//end try

                //then get the results
                if (donePosting) {
                    try {
                        results.setResult(getRequest(con));
                    } catch (IOException ex) {
                        results.setException(new CustomTaskException(CustomTaskException.NETWORK_ERROR, handleNetworkErrorResponse(con.getErrorStream())));
                    }
                }//end if

            } catch (ProtocolException ex) {
                results.setException(new CustomTaskException(CustomTaskException.NETWORK_ERROR, "ProtocolException Posting data: " + ex.getMessage()));
            } catch (Exception ex4) {
                results.setException(new CustomTaskException(CustomTaskException.EXCEPTION_ERROR, "Exception Posting data: " + ex4.getMessage()));
            }
        });

    }

    public CustomTaskResult<File> getFile(String unEncodedUrlString, String destination) {
        CustomTaskResult<File> res = new CustomTaskResult<>();
        executorService.execute(() -> {
            try {
                URL url = new URL(unEncodedUrlString);
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                res.setResult(getFile(new java.net.URL(uri.toASCIIString()), destination));
            } catch (MalformedURLException ex1) {
                res.setException(new CustomTaskException(CustomTaskException.EXCEPTION_ERROR, "MalformedURLException: " + ex1.getMessage()));
            } catch (IOException ex2) {
                res.setException(new CustomTaskException(CustomTaskException.EXCEPTION_ERROR, "IOException: " + ex2.getMessage()));
            } catch (Exception ex3) {
                res.setException(new CustomTaskException(CustomTaskException.EXCEPTION_ERROR, "Exception: " + ex3.getMessage()));
            }
        });
        return res;
    }

    protected File getFile(java.net.URL url, String destination) throws MalformedURLException, IOException {
        java.net.URLConnection conn = url.openConnection();
        OutputStream out;
        File dstfile = new File(destination);
        try (java.io.InputStream in = conn.getInputStream()) {
            out = new FileOutputStream(dstfile);
            byte[] buffer = new byte[512];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

        }
        out.close();
        return dstfile;
    }

    public CustomTaskResult<String> postFile(String url, String localFilePathAndName, String serverUsedFileName) {
        return postFile(url, new File(localFilePathAndName), serverUsedFileName);
    }

    public CustomTaskResult<String> postFile(String url, File fileToUpload, String serverUsedFileName) {
        CustomTaskResult<String> res = new CustomTaskResult<>();
        postFile(url, fileToUpload, serverUsedFileName, res);
        return res;
    }

    protected void postFile(String url, File fileToUpload, String serverUsedFileName, CustomTaskResult<String> res) {
        executorService.execute(() -> {
            try {
                java.net.HttpURLConnection con = bUseSecureConnection ? getHttpsUrlConn(url) : getHttpUrlConn(url);
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                //String boundary = "***232404jkg4220957934FW**";
                String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.

                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;

                // Use a post method.
                con.setRequestMethod("POST");
                con.setRequestProperty("Connection", "Keep-Alive");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                //try posting the file first
                try (FileInputStream fileInputStream = new FileInputStream(fileToUpload)) {

                    try (DataOutputStream dos = new DataOutputStream(con.getOutputStream())) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        //todo. this code is commented because in future we could want other post parameters 
                        //to be sent with the posted file as shown. For now only "name" parameter is being added.
                        //comment made on 01/04/2021 02:41 PM at Machakos house
//                        dos.writeBytes("Content-Disposition: form-data; name=\"" + serverFileName + "\";"
//                                + " filename=\"" + fileToUpload.getName() + "\"" + lineEnd);

                        dos.writeBytes("Content-Disposition: form-data; name=\"" + serverUsedFileName + "\";" + lineEnd);
                        dos.writeBytes(lineEnd);
                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }   //send multipart form data necesssary after file data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        dos.flush();
                    }

                    //left here
                    String str = getRequest(con);
                    res.setResult(str);

                } catch (MalformedURLException malex) {
                    res.setException(new CustomTaskException(CustomTaskException.NETWORK_ERROR, "MalformedURLException when sending file: " + malex.getMessage()));
                } catch (IOException ioe) {
                    res.setException(new CustomTaskException(CustomTaskException.NETWORK_ERROR, "IOException when sending file: " + ioe.getMessage()));
                } catch (Exception ex) {
                    res.setExceptionError(ex);
                }
            } catch (ProtocolException ex) {
                res.setException(new CustomTaskException(CustomTaskException.NETWORK_ERROR, "Protocal Exception when sending file: " + ex.getMessage()));
            } catch (Exception ex4) {
                res.setExceptionError(ex4);
            }
        });

    }

    protected String handleNetworkErrorResponse(InputStream errorStream) {
        StringBuilder errorMessage = new StringBuilder("");

        try {
            //This is done to clear the connection for reuse
            try (BufferedReader br = new BufferedReader(new InputStreamReader(errorStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    errorMessage.append(line);
                }//end while
            }//end try-with-resource
        } catch (IOException ex1) {
            LoggingUtil.e(NetworkBridge.class, "IOException in handling error response: " + ex1.getMessage());
        } catch (Exception ex2) {
            //could be cuased by delay in network response
            LoggingUtil.e(NetworkBridge.class, "Exception in handling error response: " + ex2.getMessage());
        }//end try//end try//end try//end try//end try//end try//end try//end try

        return errorMessage.toString();
    }//end method

}//end class
