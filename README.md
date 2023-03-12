# Complex
This project explores various approaches to generating the mandlebrotSet (as well as the euler set). 
My first appoach was a more OOP style using complex number objects however this was eventually optimized in mandlebrotSimple by using doubles instead. 
The final version allows for the gerneration of high quallity images of the set (e.g., 28000x15750) quickly. 
Although some of its speed is due to it's simple disign, the vast majority is due to multithreading implementation using lambda funcitons for simplicity.
