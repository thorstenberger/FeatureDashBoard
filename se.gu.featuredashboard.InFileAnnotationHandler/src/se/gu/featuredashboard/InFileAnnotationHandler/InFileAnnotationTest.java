package se.gu.featuredashboard.InFileAnnotationHandler;

import java.io.File;

public class InFileAnnotationTest {

	InFileAnnotationParser parser = new InFileAnnotationParser();
		
	public boolean setter_getter_Test(){
		System.out.println("Test setter_getter is starting.");
		if(parser.setFeatureLocation("A", 0)) 
			return false;
		
		parser.setFeatureLocation("A", 1);
		parser.setFeatureLocation("B", 2);
		parser.setFeatureLocation("C", 3,10);
		parser.setFeatureLocation("B", 4);
		parser.setFeatureLocation("A", 5,9);
		
		if(!(parser.getLocations("A").size() ==2 &&
			 parser.getLocations("A").get(0)[0].equals(1) && parser.getLocations("A").get(0).length==1 &&
		     parser.getLocations("A").get(1)[0].equals(5) && parser.getLocations("A").get(1).length==2 &&
		     parser.getLocations("A").get(1)[1].equals(9)	))
			return false;
		
		if(!(parser.getLocations("B").size() ==2 &&
			 parser.getLocations("B").get(0)[0].equals(2) && parser.getLocations("B").get(0).length==1 &&
			 parser.getLocations("B").get(1)[0].equals(4) && parser.getLocations("B").get(1).length==1))
			return false;
		
		if(!(parser.getLocations("C").size() ==1 &&
			 parser.getLocations("C").get(0)[0].equals(3) && parser.getLocations("C").get(0).length==2 &&
			 parser.getLocations("C").get(0)[1].equals(10)))
			return false;
		
		parser.getLocations("C").get(0)[0] =5;	//the field must be modified only by setters
		if(parser.getLocations("C").get(0)[0].equals(5))
			return false;
			
		System.out.println("Test setter_getter is done successfully.\n");
		parser.clearFeaturesAndLocations();
		return true;
	}
	
	public boolean readingFromFileTest2(){
		System.out.println("Test readingFromFile2 is starting.");
		if(! new File("src/se/gu/featuredashboard/InFileAnnotationHandler/testExample_Server.js").exists()){
			System.out.println("a needed test file does not exist.");
			return false;
		}
		parser.readInFileAnnotation("src/se/gu/featuredashboard/InFileAnnotationHandler/testExample_Server.js"); // javascript example including 4 features
		if(parser.getAllFeatures().size() != 4){ 
			System.out.println(parser.toString());
			return false;
		
		}
		if(! (parser.getLocations("fileUpload").get(0)[0] == 184) || !(parser.getLocations("fileUpload").get(0)[1]==266) ) return false;
		if(! (parser.getLocations("fileProcessing").get(0)[0] == 199) || !(parser.getLocations("fileProcessing").get(0)[1]==262) ) return false;
		if(! (parser.getLocations("polling").get(0)[0] == 267) || !(parser.getLocations("polling").get(0)[1]==441) ) return false;
		if(! (parser.getLocations("cancellation").get(0)[0] == 413) || !(parser.getLocations("cancellation").get(0)[1]==433) ) return false;
		System.out.println("Test readingFromFile2 is done successfully.\n");
		parser.clearFeaturesAndLocations();
		return true;
	}
	
	public boolean readingFromFileTest1(){
		System.out.println("Test readingFromFile1 is starting.");
		if(! new File("src/se/gu/featuredashboard/InFileAnnotationHandler/testExample_Ccode.c").exists()){
			System.out.println("a needed test file does not exist.");
			return false;
		}
		parser.readInFileAnnotation("src/se/gu/featuredashboard/InFileAnnotationHandler/testExample_Ccode.c"); // c example including 2 features
		if(parser.getAllFeatures().size() != 2) return false;
		if( parser.getLocations("feature1").get(0)[0] != 5 ) return false;
		if(! (parser.getLocations("feature2").get(0)[0] == 9) || !(parser.getLocations("feature2").get(0)[1]==12) ) return false;
		System.out.println("Test readingFromFile1 is done successfully.\n");
		parser.clearFeaturesAndLocations();
		return true;
	}
	
	public boolean getAllFeaturesTest(){
		System.out.println("Test getAllFeaturesis starting.");
		parser.setFeatureLocation("feature1", 2);
		parser.setFeatureLocation("feature1", 5,10);
		parser.setFeatureLocation("feature2", 3,6);
		parser.setFeatureLocation("feature3", 7);
		
		if(parser.getAllFeatures().size()!=3){
			System.out.println(parser.getAllFeatures().size());
			return false;
		}
		if(parser.getAllFeatureLocations().get("feature1").size()!=2 || 
		   parser.getAllFeatureLocations().get("feature1").get(0)[0]!=2 ||
		   parser.getAllFeatureLocations().get("feature1").get(1)[0]!=5 ||
		   parser.getAllFeatureLocations().get("feature1").get(1)[1]!=10) 
			return false;
		if(parser.getAllFeatureLocations().get("feature2").size()!=1 || 
		   parser.getAllFeatureLocations().get("feature2").get(0)[0]!=3 ||
		   parser.getAllFeatureLocations().get("feature2").get(0)[1]!=6) 
			return false;
		if(parser.getAllFeatureLocations().get("feature3").size()!=1 || 
		   parser.getAllFeatureLocations().get("feature3").get(0)[0]!=7)
			return false;
		
		System.out.println("Test getAllFeatures is done successfully.\n");
		parser.clearFeaturesAndLocations();
		return true;
	}
	
	public void printTest(){
		System.out.println("Test printTest is starting.");
		System.out.println("Showing a feature Model including 3 features, which\n"+
							"feature1 is in locations 2, (5,10),\n"+
							"feature2 is in locations (3,6), and\n"+
							"feature3 is in location 7.\n");	
		parser.setFeatureLocation("feature1", 2);
		parser.setFeatureLocation("feature1", 5,10);
		parser.setFeatureLocation("feature2", 3,6);
		parser.setFeatureLocation("feature3", 7);
		System.out.println("Printing the feature model:");
		System.out.println(parser.toString());
		System.out.println("Test printTest is done successfully.\n");
		parser.clearFeaturesAndLocations();
	}
	
	public static void main(String[] args) {
		InFileAnnotationTest parserTester = new InFileAnnotationTest();
		parserTester.setter_getter_Test();
		parserTester.getAllFeaturesTest();
		parserTester.printTest();
		parserTester.readingFromFileTest1();
		parserTester.readingFromFileTest2();
	}

}
