package anticaps.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import filter.MessagePipeline;

public class FilterTests {
    @Test
    public void testFilterMinimum() {
        MessagePipeline pipeline = mock(MessagePipeline.class);

        when(pipeline.getRatio()).thenReturn(50.0);
        when(pipeline.getLowerLimit()).thenReturn(5);
        when(pipeline.filter(any(String.class))).thenCallRealMethod();
        when(pipeline.countPercentage(any(String.class))).thenCallRealMethod();

        String message = "Hello";
        String filtered = pipeline.filter(message);

        assertEquals(message, filtered);

        message = "AAA";
        filtered = pipeline.filter(message);

        assertEquals(message, filtered);

        message = "AAAAAAAA";
        filtered = pipeline.filter(message);

        assertEquals("Aaaaaaaa", filtered);
    }

    @Test
    public void testSingleWord() {
        MessagePipeline pipeline = mock(MessagePipeline.class);

        when(pipeline.getRatio()).thenReturn(50.0);
        when(pipeline.getLowerLimit()).thenReturn(5);
        when(pipeline.filter(any(String.class))).thenCallRealMethod();
        when(pipeline.countPercentage(any(String.class))).thenCallRealMethod();

        String message = "bOREGFKREPFKREOPFKERFER";
        String filtered = pipeline.filter(message);

        assertEquals("boregfkrepfkreopfkerfer", filtered);
    }

    @Test
    public void testWordChunking() {
        MessagePipeline pipeline = mock(MessagePipeline.class);

        when(pipeline.getRatio()).thenReturn(60.0);
        when(pipeline.getLowerLimit()).thenReturn(5);
        when(pipeline.filter(any(String.class))).thenCallRealMethod();
        when(pipeline.countPercentage(any(String.class))).thenCallRealMethod();

        String message = "Hello World";
        String filtered = pipeline.filter(message);

        assertEquals(message, filtered);

        // Test case with special characters and whitespace (should ignore them in percentage calculation)
        message = ": :AAAAA AAaaa: :";
        filtered = pipeline.filter(message);

        assertEquals(": :Aaaaa aaaaa: :", filtered);

        // Test case with sentences using Title Case
        when(pipeline.getRatio()).thenReturn(10.0);
        message = "The Quick Brown Fox Jumps Over Lazy Dogs Near The River";
        filtered = pipeline.filter(message);

        assertEquals("The quick brown fox jumps over lazy dogs near the river", filtered);
    }
}
