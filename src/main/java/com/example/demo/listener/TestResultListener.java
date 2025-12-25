package com.example.demo.listener;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestResultListener implements ITestListener {
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test started: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test passed: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getMethod().getMethodName());
        System.out.println("Failure reason: " + result.getThrowable().getMessage());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test skipped: " + result.getMethod().getMethodName());
    }
}