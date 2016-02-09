package uk.co.ben_gibson.repositorymapper.RepositoryProvider.UrlFactory;

import com.intellij.testFramework.UsefulTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.co.ben_gibson.repositorymapper.Context.Context;
import uk.co.ben_gibson.repositorymapper.RepositoryProvider.Context.ContextTestUtil;
import uk.co.ben_gibson.repositorymapper.UrlFactory.BitBucketUrlFactory;
import uk.co.ben_gibson.repositorymapper.UrlFactory.UrlFactoryException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Tests the BitBucket Url factory.
 */
@RunWith(Parameterized.class)
public class BitBucketUrlFactoryTest extends UsefulTestCase
{

    private Context context;
    private String expectedUrl;

    /**
     * Constructor.
     *
     * @param context      The context.
     * @param expectedUrl  The expected url to be returned from the context.
     *
     */
    public BitBucketUrlFactoryTest(Context context, String expectedUrl)
    {
        this.context     = context;
        this.expectedUrl = expectedUrl;
    }


    /**
     * Tests the url factory creates the correct url from a given context.
     */
    @Test
    public void testGetUrlFromContext() throws URISyntaxException, UrlFactoryException, MalformedURLException, UnsupportedEncodingException
    {
        assertEquals(this.expectedUrl, this.getBitBucketUrlFactory().getUrlFromContext(this.context).toString());
    }


    /**
     * Acts as a data provider for contexts and their expected url result.
     *
     * @return Collection
     */
    @Parameterized.Parameters
    public static Collection contexts() throws MalformedURLException, UrlFactoryException
    {
        return Arrays.asList(new Object[][] {
            {
                ContextTestUtil.getMockedContext("https://bitbucket.org/foo/bar", "master", "/src/Bar.java"),
                "https://bitbucket.org/foo/bar/src/HEAD/src/Bar.java?at=master"
            },
            {
                ContextTestUtil.getMockedContext("https://bitbucket.org/foo/bar", "foo-bar", "/src/FooBar/Bar.java", 10),
                "https://bitbucket.org/foo/bar/src/HEAD/FooBar/Bar.java?at=foo-bar#Bar.java-10"
            },
            {
                ContextTestUtil.getMockedContext("https://bitbucket.org/foo bar/bar", "misc/foo-bar", "/src/Foo Bar/Bar.java", 0),
                "https://bitbucket.com/foo%20bar/bar/src/HEAD/Foo%20Bar/Bar.java?at=misc%2ffoo-bar#Bar.java-0"
            },
        });
    }


    /**
     * Get the url factory.
     *
     * @return BitBucketUrlFactory
     */
    public BitBucketUrlFactory getBitBucketUrlFactory()
    {
        return new BitBucketUrlFactory();
    }
}