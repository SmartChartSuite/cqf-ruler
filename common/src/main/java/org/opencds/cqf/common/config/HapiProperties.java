package org.opencds.cqf.common.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.annotations.VisibleForTesting;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.SearchStyleEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;

public class HapiProperties {
    static final String ALLOW_EXTERNAL_REFERENCES = "allow_external_references";
    static final String ALLOW_MULTIPLE_DELETE = "allow_multiple_delete";
    static final String ALLOW_PLACEHOLDER_REFERENCES = "allow_placeholder_references";
    static final String ENFORCE_REFERENTIAL_INTEGRITY_ON_WRITE = "enforce_referential_integrity_on_write";
    static final String ENFORCE_REFERENTIAL_INTEGRITY_ON_DELETE = "enforce_referential_integrity_on_delete";
    static final String REUSE_CACHED_SEARCH_RESULTS_MILLIS = "reuse_cached_search_results_millis";
    static final String DATASOURCE_DRIVER = "datasource.driver";
    static final String DATASOURCE_MAX_POOL_SIZE = "datasource.max_pool_size";
    static final String DATASOURCE_PASSWORD = "datasource.password";
    static final String DATASOURCE_URL = "datasource.url";
    static final String DATASOURCE_USERNAME = "datasource.username";
    static final String DEFAULT_ENCODING = "default_encoding";
    static final String DEFAULT_PAGE_SIZE = "default_page_size";
    static final String DEFAULT_PRETTY_PRINT = "default_pretty_print";
    static final String ETAG_SUPPORT = "etag_support";
    static final String FHIR_VERSION = "fhir_version";
    static final String HAPI_PROPERTIES = "hapi.properties";
    static final String LOGGER_ERROR_FORMAT = "logger.error_format";
    static final String LOGGER_FORMAT = "logger.format";
    static final String LOGGER_LOG_EXCEPTIONS = "logger.log_exceptions";
    static final String LOGGER_NAME = "logger.name";
    static final String MAX_FETCH_SIZE = "max_fetch_size";
    static final String MAX_PAGE_SIZE = "max_page_size";
    static final String PERSISTENCE_UNIT_NAME = "persistence_unit_name";
    static final String SERVER_ADDRESS = "server_address";
    static final String SERVER_BASE = "server.base";
    static final String SERVER_ID = "server.id";
    static final String SERVER_NAME = "server.name";
    static final String SUBSCRIPTION_EMAIL_ENABLED = "subscription.email.enabled";
    static final String SUBSCRIPTION_RESTHOOK_ENABLED = "subscription.resthook.enabled";
    static final String SUBSCRIPTION_WEBSOCKET_ENABLED = "subscription.websocket.enabled";
    static final String TEST_PORT = "test.port";
    static final String TESTER_CONFIG_REFUSE_TO_FETCH_THIRD_PARTY_URLS = "tester.config.refuse_to_fetch_third_party_urls";
    static final String CORS_ENABLED = "cors.enabled";
    static final String CORS_ALLOWED_ORIGIN = "cors.allowed_origin";
    static final String ALLOW_CONTAINS_SEARCHES = "allow_contains_searches";
    static final String ALLOW_OVERRIDE_DEFAULT_SEARCH_PARAMS = "allow_override_default_search_params";
    static final String EMAIL_FROM = "email.from";
    static final String OAUTH_ENABLED = "oauth.enabled";
    static final String OAUTH_SECURITY_CORS = "oauth.securityCors";
    static final String OAUTH_SECURITY_URL = "oauth.securityUrl";
    static final String OAUTH_SECURITY_EXT_AUTH_URL = "oauth.securityExtAuthUrl";
    static final String OAUTH_SECURITY_EXT_AUTH_VALUE_URI = "oauth.securityExtAuthValueUri";
    static final String OAUTH_SECURITY_EXT_TOKEN_URL = "oauth.securityExtTokenUrl";
    static final String OAUTH_SECURITY_EXT_TOKEN_VALUE_URI = "oauth.securityExtTokenValueUri";
    static final String OAUTH_SERVICE_SYSTEM = "oauth.serviceSystem";
    static final String OAUTH_SERVICE_CODE = "oauth.serviceCode";
    static final String OAUTH_SERVICE_DISPLAY = "oauth.serviceDisplay";
    static final String OAUTH_SERVICE_TEXT = "oauth.serviceText";
    static final String QUESTIONNAIRE_RESPONSE_ENABLED = "questionnaireResponseExtract.enabled";
    static final String QUESTIONNAIRE_RESPONSE_ENDPOINT = "questionnaireResponseExtract.endpoint";
    static final String QUESTIONNAIRE_RESPONSE_USERNAME = "questionnaireResponseExtract.username";
    static final String QUESTIONNAIRE_RESPONSE_PASSWORD = "questionnaireResponseExtract.password";

    static final String OBSERVATION_TRANSFORM_ENABLED = "observationTransform.enabled";
    static final String OBSERVATION_TRANSFORM_USERNAME = "observationTransform.username";
    static final String OBSERVATION_TRANSFORM_PASSWORD = "observationTransform.password";
    static final String OBSERVATION_TRANSFORM_REPLACE_CODE = "observationTransform.replaceCode";

    static final String CDSHOOKS_FHIRSERVER_MAXCODESPERQUERY = "cds_hooks.fhirServer.maxCodesPerQuery";
    static final String CDSHOOKS_FHIRSERVER_EXPANDVALUESETS = "cds_hooks.fhirServer.expandValueSets";
    static final String CDSHOOKS_FHIRSERVER_SEARCHSTYLE= "cds_hooks.fhirServer.searchStyle";
    static final String CDSHOOKS_PREFETCH_MAXURILENGTH= "cds_hooks.prefetch.maxUriLength";
    static final String CQL_LOGGING_ENABLED = "hapi.fhir.cql_logging_enabled";

    private static Properties properties;

    /*
     * Force the configuration to be reloaded
     */
    public static void forceReload() {
        properties = null;
        getProperties();
    }

    /**
     * This is mostly here for unit tests. Use the actual properties file to set
     * values
     */
    @VisibleForTesting
    public static void setProperty(String theKey, String theValue) {
        getProperties().setProperty(theKey, theValue);
    }

    public static Properties getProperties() {
        if (properties == null) {
            // Load the configurable properties file
            try (InputStream in = HapiProperties.class.getClassLoader().getResourceAsStream(HAPI_PROPERTIES)) {
                HapiProperties.properties = new Properties();
                HapiProperties.properties.load(in);
            } catch (Exception e) {
                throw new ConfigurationException("Could not load HAPI properties", e);
            }

            Properties overrideProps = loadOverrideProperties();
            if (overrideProps != null) {
                properties.putAll(overrideProps);
            }
        }

        return properties;
    }

    /**
     * If a configuration file path is explicitly specified via
     * -Dhapi.properties=<path>, the properties there will be used to override the
     * entries in the default hapi.properties file (currently under WEB-INF/classes)
     * 
     * @return properties loaded from the explicitly specified configuraiton file if
     *         there is one, or null otherwise.
     */
    private static Properties loadOverrideProperties() {
        String confFile = System.getProperty(HAPI_PROPERTIES + "." + HapiProperties.getProperty(FHIR_VERSION));
        if (confFile == null) {
            confFile = System.getProperty(HAPI_PROPERTIES);
        }
        if (confFile != null) {
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(confFile));
                return props;
            } catch (Exception e) {
                throw new ConfigurationException("Could not load HAPI properties file: " + confFile, e);
            }
        }

        return null;
    }

    public static String getProperty(String propertyName) {
        Properties properties = HapiProperties.getProperties();

        if (properties != null) {
            return properties.getProperty(propertyName);
        }

        return null;
    }

    public static String getProperty(String propertyName, String defaultValue) {
        Properties properties = HapiProperties.getProperties();

        if (properties != null) {
            String value = properties.getProperty(propertyName);

            if (value != null && value.length() > 0) {
                return value;
            }
        }

        return defaultValue;
    }

    private static Boolean getBooleanProperty(String propertyName, Boolean defaultValue) {
        String value = HapiProperties.getProperty(propertyName);

        if (value == null || value.length() == 0) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    private static Integer getIntegerProperty(String propertyName, Integer defaultValue) {
        String value = HapiProperties.getProperty(propertyName);

        if (value == null || value.length() == 0) {
            return defaultValue;
        }

        return Integer.parseInt(value);
    }

    public static FhirVersionEnum getFhirVersion() {
        String fhirVersionString = HapiProperties.getProperty(FHIR_VERSION);

        if (fhirVersionString != null && fhirVersionString.length() > 0) {
            return FhirVersionEnum.valueOf(fhirVersionString);
        }

        return FhirVersionEnum.R4;
    }

    public static ETagSupportEnum getEtagSupport() {
        String etagSupportString = HapiProperties.getProperty(ETAG_SUPPORT);

        if (etagSupportString != null && etagSupportString.length() > 0) {
            return ETagSupportEnum.valueOf(etagSupportString);
        }

        return ETagSupportEnum.ENABLED;
    }

    public static EncodingEnum getDefaultEncoding() {
        String defaultEncodingString = HapiProperties.getProperty(DEFAULT_ENCODING);

        if (defaultEncodingString != null && defaultEncodingString.length() > 0) {
            return EncodingEnum.valueOf(defaultEncodingString);
        }

        return EncodingEnum.JSON;
    }

    public static Boolean getDefaultPrettyPrint() {
        return HapiProperties.getBooleanProperty(DEFAULT_PRETTY_PRINT, true);
    }

    public static String getServerAddress() {
        return HapiProperties.getProperty(SERVER_ADDRESS);
    }

    public static Integer getDefaultPageSize() {
        return HapiProperties.getIntegerProperty(DEFAULT_PAGE_SIZE, 20);
    }

    public static Integer getMaximumPageSize() {
        return HapiProperties.getIntegerProperty(MAX_PAGE_SIZE, 200);
    }

    public static Integer getMaximumFetchSize() {
        return HapiProperties.getIntegerProperty(MAX_FETCH_SIZE, Integer.MAX_VALUE);
    }

    public static String getPersistenceUnitName() {
        return HapiProperties.getProperty(PERSISTENCE_UNIT_NAME, "HAPI_PU");
    }

    public static String getLoggerName() {
        return HapiProperties.getProperty(LOGGER_NAME, "fhirtest.access");
    }

    public static String getLoggerFormat() {
        return HapiProperties.getProperty(LOGGER_FORMAT,
                "Path[${servletPath}] Source[${requestHeader.x-forwarded-for}] Operation[${operationType} ${operationName} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}] ResponseEncoding[${responseEncodingNoDefault}]");
    }

    public static String getLoggerErrorFormat() {
        return HapiProperties.getProperty(LOGGER_ERROR_FORMAT, "ERROR - ${requestVerb} ${requestUrl}");
    }

    public static Boolean getLoggerLogExceptions() {
        return HapiProperties.getBooleanProperty(LOGGER_LOG_EXCEPTIONS, true);
    }

    public static String getDataSourceDriver() {
        return HapiProperties.getProperty(DATASOURCE_DRIVER, "org.apache.derby.jdbc.EmbeddedDriver");
    }

    // public static Object getDriver() {
    // return new org.apache.derby.jdbc.EmbeddedDriver();
    // }

    public static Integer getDataSourceMaxPoolSize() {
        return HapiProperties.getIntegerProperty(DATASOURCE_MAX_POOL_SIZE, 10);
    }

    public static String getDataSourceUrl() {
        return HapiProperties.getProperty(DATASOURCE_URL,
                "jdbc:derby:directory:target/jpaserver_derby_files;create=true");
    }

    public static String getDataSourceUsername() {
        return HapiProperties.getProperty(DATASOURCE_USERNAME);
    }

    public static String getDataSourcePassword() {
        return HapiProperties.getProperty(DATASOURCE_PASSWORD);
    }

    public static Boolean getAllowMultipleDelete() {
        return HapiProperties.getBooleanProperty(ALLOW_MULTIPLE_DELETE, false);
    }

    public static Boolean getAllowExternalReferences() {
        return HapiProperties.getBooleanProperty(ALLOW_EXTERNAL_REFERENCES, false);
    }

    public static Boolean getExpungeEnabled() {
        return HapiProperties.getBooleanProperty("expunge_enabled", true);
    }

    public static Integer getTestPort() {
        return HapiProperties.getIntegerProperty(TEST_PORT, 0);
    }

    public static Boolean getTesterConfigRefustToFetchThirdPartyUrls() {
        return HapiProperties.getBooleanProperty(TESTER_CONFIG_REFUSE_TO_FETCH_THIRD_PARTY_URLS, false);
    }

    public static Boolean getCorsEnabled() {
        return HapiProperties.getBooleanProperty(CORS_ENABLED, true);
    }

    public static String getCorsAllowedOrigin() {
        return HapiProperties.getProperty(CORS_ALLOWED_ORIGIN, "*");
    }

    public static String getServerBase() {
        return HapiProperties.getProperty(SERVER_BASE, "/cqf-ruler-" + HapiProperties.getFhirVersion().toString().toLowerCase() + "/fhir");
    }

    public static String getServerName() {
        return HapiProperties.getProperty(SERVER_NAME, "Local Tester");
    }

    public static String getServerId() {
        return HapiProperties.getProperty(SERVER_ID, "home");
    }

    public static Boolean getAllowPlaceholderReferences() {
        return HapiProperties.getBooleanProperty(ALLOW_PLACEHOLDER_REFERENCES, true);
    }

    public static Boolean getSubscriptionEmailEnabled() {
        return HapiProperties.getBooleanProperty(SUBSCRIPTION_EMAIL_ENABLED, false);
    }

    public static Boolean getSubscriptionRestHookEnabled() {
        return HapiProperties.getBooleanProperty(SUBSCRIPTION_RESTHOOK_ENABLED, false);
    }

    public static Boolean getSubscriptionWebsocketEnabled() {
        return HapiProperties.getBooleanProperty(SUBSCRIPTION_WEBSOCKET_ENABLED, false);
    }

    public static Boolean getAllowContainsSearches() {
        return HapiProperties.getBooleanProperty(ALLOW_CONTAINS_SEARCHES, true);
    }

    public static Boolean getAllowOverrideDefaultSearchParams() {
        return HapiProperties.getBooleanProperty(ALLOW_OVERRIDE_DEFAULT_SEARCH_PARAMS, true);
    }

    public static String getEmailFrom() {
        return HapiProperties.getProperty(EMAIL_FROM, "some@test.com");
    }

    public static Boolean getEmailEnabled() {
        return HapiProperties.getBooleanProperty("email.enabled", false);
    }

    public static String getEmailHost() {
        return HapiProperties.getProperty("email.host");
    }

    public static Integer getEmailPort() {
        return HapiProperties.getIntegerProperty("email.port", 0);
    }

    public static String getEmailUsername() {
        return HapiProperties.getProperty("email.username");
    }

    public static String getEmailPassword() {
        return HapiProperties.getProperty("email.password");
    }

    public static Long getReuseCachedSearchResultsMillis() {
        String value = HapiProperties.getProperty(REUSE_CACHED_SEARCH_RESULTS_MILLIS, "-1");
        return Long.valueOf(value);
    }

    public static boolean getEnforceReferentialIntegrityOnDelete() {
        return HapiProperties.getBooleanProperty(ENFORCE_REFERENTIAL_INTEGRITY_ON_DELETE, true);
    }

    public static boolean getEnforceReferentialIntegrityOnWrite() {
        return HapiProperties.getBooleanProperty(ENFORCE_REFERENTIAL_INTEGRITY_ON_WRITE, true);
    }

    // ************************* OAuth
    // *******************************************************
    public static Boolean getOAuthEnabled() {
        return HapiProperties.getBooleanProperty(OAUTH_ENABLED, false);
    }

    public static Boolean getOauthSecurityCors() {
        return HapiProperties.getBooleanProperty(OAUTH_SECURITY_CORS, true);
    }

    public static String getOauthSecurityUrl() {
        return HapiProperties.getProperty(OAUTH_SECURITY_URL, "");
    }

    public static String getOauthSecurityExtAuthUrl() {
        return HapiProperties.getProperty(OAUTH_SECURITY_EXT_AUTH_URL, "");
    }

    public static String getOauthSecurityExtAuthValueUri() {
        return HapiProperties.getProperty(OAUTH_SECURITY_EXT_AUTH_VALUE_URI, "");
    }

    public static String getOauthSecurityExtTokenUrl() {
        return HapiProperties.getProperty(OAUTH_SECURITY_EXT_TOKEN_URL, "");
    }

    public static String getOauthSecurityExtTokenValueUri() {
        return HapiProperties.getProperty(OAUTH_SECURITY_EXT_TOKEN_VALUE_URI, "");
    }

    public static String getOauthServiceSystem() {
        return HapiProperties.getProperty(OAUTH_SERVICE_SYSTEM, "");
    }

    public static String getOauthServiceCode() {
        return HapiProperties.getProperty(OAUTH_SERVICE_CODE, "");
    }

    public static String getOauthServiceDisplay() {
        return HapiProperties.getProperty(OAUTH_SERVICE_DISPLAY, "");
    }

    public static String getOauthServiceText() {
        return HapiProperties.getProperty(OAUTH_SERVICE_TEXT, "");
    }

    public static Boolean getQuestionnaireResponseExtractEnabled() {
        return HapiProperties.getBooleanProperty(QUESTIONNAIRE_RESPONSE_ENABLED, false);
    }

    public static String getQuestionnaireResponseExtractEndpoint() {
        return HapiProperties.getProperty(QUESTIONNAIRE_RESPONSE_ENDPOINT);
    }

    public static String getQuestionnaireResponseExtractUserName() {
        return HapiProperties.getProperty(QUESTIONNAIRE_RESPONSE_USERNAME);
    }

    public static String getQuestionnaireResponseExtractPassword() {
        return HapiProperties.getProperty(QUESTIONNAIRE_RESPONSE_PASSWORD);
    }

    public static Boolean getObservationTransformEnabled() {
        return HapiProperties.getBooleanProperty(OBSERVATION_TRANSFORM_ENABLED, false);
    }

    public static String getObservationTransformUsername() {
        return HapiProperties.getProperty(OBSERVATION_TRANSFORM_USERNAME);
    }

    public static String getObservationTransformPassword() {
        return HapiProperties.getProperty(OBSERVATION_TRANSFORM_PASSWORD);
    }

    public static Boolean getObservationTransformReplaceCode() {
        return HapiProperties.getBooleanProperty(OBSERVATION_TRANSFORM_REPLACE_CODE, false);
    }

    // ************************* CDS_HOOKS ****************
    public static Integer getCdsHooksFhirServerMaxCodesPerQuery() {
        return HapiProperties.getIntegerProperty(CDSHOOKS_FHIRSERVER_MAXCODESPERQUERY, 64);
    }

    public static Boolean getCdsHooksFhirServerExpandValueSets() {
        return HapiProperties.getBooleanProperty(CDSHOOKS_FHIRSERVER_EXPANDVALUESETS, true);
    }

    public static SearchStyleEnum getCdsHooksFhirServerSearchStyleEnum() {
        String searchStyleEnumString = HapiProperties.getProperty(CDSHOOKS_FHIRSERVER_SEARCHSTYLE);

        if (searchStyleEnumString != null && searchStyleEnumString.length() > 0) {
            return SearchStyleEnum.valueOf(searchStyleEnumString);
        }

        return SearchStyleEnum.GET;
    }
    public static Integer getCdsHooksPreFetchMaxUriLength() { return HapiProperties.getIntegerProperty(CDSHOOKS_PREFETCH_MAXURILENGTH, 8000);}

    public static Boolean getCqlLoggingEnabled() {
        return HapiProperties.getBooleanProperty(CQL_LOGGING_ENABLED, true);
    }

}
