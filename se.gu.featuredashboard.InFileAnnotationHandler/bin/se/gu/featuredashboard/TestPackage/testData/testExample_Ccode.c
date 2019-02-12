#include <stdio.h>

int main()
{
	//&line[feature1]
	int a =1;
    printf("we have one feature named 'feature1' annotated in line 6");
    
	//&begin[feature2]
    String b= "chalmers";
    firstManipulate(b);
	//&end[feature2]
	 
	 printf("Some other code in these lines!");
	 
	//&begin[feature2]
    seconfManipulate(b);
	//&end[feature2]

	//adding a feature, from the js file, multipleline
	//&begin[fileUpload]
	someCode1();
	someCode2();
	//&end[fileUpload]
	
	//adding a feature, from the js file, singleline
	//&line[fileProcessing]
	processingCode();

    return 0;
}
