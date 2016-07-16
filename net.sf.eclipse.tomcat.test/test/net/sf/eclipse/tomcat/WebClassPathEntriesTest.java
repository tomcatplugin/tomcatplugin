/* The MIT License
 * (c) Copyright Sysdeo SA 2001-2002
 * (c) Copyright Eclipse Tomcat Plugin 2014-2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.sf.eclipse.tomcat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class WebClassPathEntriesTest {

	@Test
	public void testWebClassPathEntriesBasicParsing() {
		String xml = "";
		WebClassPathEntries entries = WebClassPathEntries.xmlUnmarshal(xml);
		assertThat("invalid xml must result in null object", entries, nullValue());

		xml = "<webClassPathEntries></webClassPathEntries>";
		entries = WebClassPathEntries.xmlUnmarshal(xml);
		assertThat("valid xml must result in an object", entries, notNullValue());
		assertThat("entries must be empty", Integer.valueOf(entries.size()), equalTo(Integer.valueOf(0)));
	}

	@Test
	public void testWebClassPathEntriesEmpty() {
		String xml = "<root><webClassPathEntries>\n</webClassPathEntries>\n</root>";		
		WebClassPathEntries entries = WebClassPathEntries.xmlUnmarshal(xml);
		assertThat("valid xml must result in an object", entries, notNullValue());
		assertThat("entries must be empty", Integer.valueOf(entries.size()), equalTo(Integer.valueOf(0)));
	}

	@Test
	public void testWebClassPathEntriesWithEntries() {
		String xml = "<webClassPathEntries><webClassPathEntry>abc</webClassPathEntry></webClassPathEntries>";
		WebClassPathEntries entries = WebClassPathEntries.xmlUnmarshal(xml);
		assertThat("valid xml must result in an object", entries, notNullValue());
		assertThat("entries must have one entry", Integer.valueOf(entries.size()), equalTo(Integer.valueOf(1)));
		assertThat(entries.getWebClassPathEntry(0), equalTo("abc"));

		xml = "<webClassPathEntries>\n<webClassPathEntry>abc</webClassPathEntry>\n<webClassPathEntry>def</webClassPathEntry>\n<webClassPathEntry>123</webClassPathEntry>\nxxxxx</webClassPathEntries>\n";
		entries = WebClassPathEntries.xmlUnmarshal(xml);
		assertThat("valid xml must result in an object", entries, notNullValue());
		assertThat("entries must have three entry", Integer.valueOf(entries.size()), equalTo(Integer.valueOf(3)));
		assertThat(entries.getWebClassPathEntry(0), equalTo("abc"));
		assertThat(entries.getWebClassPathEntry(1), equalTo("def"));
		assertThat(entries.getWebClassPathEntry(2), equalTo("123"));
	}

	@Test
	public void testWebClassPathEntriesRoundTrip() {
		String xml = "<webClassPathEntries>\n<webClassPathEntry>abc</webClassPathEntry>\n<webClassPathEntry>def</webClassPathEntry>\n<webClassPathEntry>123</webClassPathEntry>\nxxxxx</webClassPathEntries>\n";
		WebClassPathEntries entries = WebClassPathEntries.xmlUnmarshal(xml);

		xml = "<webClassPathEntries>\n<webClassPathEntry>abc</webClassPathEntry>\n<webClassPathEntry>def</webClassPathEntry>\n<webClassPathEntry>123</webClassPathEntry>\n</webClassPathEntries>\n";
		String gen = entries.xmlMarshal();
		assertThat(gen, equalTo(xml));
	}

}
