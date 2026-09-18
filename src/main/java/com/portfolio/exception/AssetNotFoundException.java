package com.portfolio.exception;

/**
 * Custom exception for asset-related errors.
 * Demonstrates: Java Exception Handling, Custom Exceptions (Unit 3)
 */
public class AssetNotFoundException extends Exception {
    private int assetId;

    public AssetNotFoundException(String message) {
        super(message);
    }

    public AssetNotFoundException(int assetId) {
        super("Asset with ID " + assetId + " not found in portfolio.");
        this.assetId = assetId;
    }

    public int getAssetId() {
        return assetId;
    }
}
