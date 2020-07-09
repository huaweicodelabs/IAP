/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.iap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.iap.Iap
import com.huawei.hms.iap.IapApiException
import com.huawei.hms.iap.IapClient
import com.huawei.hms.iap.entity.*
import com.huawei.iap.adapters.ProductsListAdapter
import com.huawei.iap.callbacks.ProductItemClick
import com.huawei.iap.common.CipherUtil
import com.huawei.iap.models.ProductsListModel
import kotlinx.android.synthetic.main.activity_demo.*
import org.json.JSONException
import java.util.*

class DemoActivity : AppCompatActivity() {

    companion object{
        private val REQ_CODE_BUY = 4002
        private val TAG = "DemoActivity"
    }
    private val productsListModels = ArrayList<ProductsListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        itemlist.layoutManager = LinearLayoutManager(this)
        loadProduct()
    }

    /**
     * Load products information and show the products
     */
    private fun loadProduct() {
        // obtain in-app product details configured in AppGallery Connect, and then show the products
        val iapClient = Iap.getIapClient(this)
        val task =iapClient.obtainProductInfo(createProductInfoReq())
        task.addOnSuccessListener { result ->
            if (result != null && !result.productInfoList.isEmpty()) {
                showProduct(result.productInfoList)
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, e.message.toString())
            if (e is IapApiException) {
                val returnCode = e.statusCode
                if (returnCode == OrderStatusCode.ORDER_HWID_NOT_LOGIN) {
                    Toast.makeText(this,"Please sign in to the app with a HUAWEI ID.",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createProductInfoReq(): ProductInfoReq? {
        // In-app product type contains:
        // 0: consumable
        // 1: non-consumable
        // 2: auto-renewable subscription
        val req = ProductInfoReq()
        req?.let { productDetails ->
            productDetails.priceType = IapClient.PriceType.IN_APP_CONSUMABLE
            val productIds = ArrayList<String>()
            // Pass in the item_productId list of products to be queried.
            // The product ID is the same as that set by a developer when configuring product information in AppGallery Connect.
            productIds.add("Consumable_1")
            productDetails.productIds = productIds
        }
        return req
    }

    /**
     * to show the products
     * @param productInfoList Product list
     */
    private fun showProduct(productInfoList: List<ProductInfo>) {
        for (productInfo in productInfoList) {

            var productsinfo = ProductsListModel(productInfo.productName,productInfo.price,productInfo.productId,R.drawable.blue_ball)
            productsListModels.add(productsinfo)

            val adapter = ProductsListAdapter(productsListModels, productItemClick)
            itemlist.adapter=adapter
        }
    }
    var productItemClick: ProductItemClick = object : ProductItemClick {

        override fun onClick(data: ProductsListModel?) {
            val productId: String = data?.id.toString()
            Log.d("productId",""+productId)
            gotoPay(this@DemoActivity, productId, IapClient.PriceType.IN_APP_CONSUMABLE)
        }
    }
    /**
     * create orders for in-app products in the PMS.
     * @param activity indicates the activity object that initiates a request.
     * @param productId ID list of products to be queried. Each product ID must exist and be unique in the current app.
     * @param type  In-app product type.
     */
    private fun gotoPay(activity: Activity,productId: String?,type: Int) {

        Log.i(TAG,"call createPurchaseIntent")
        val mClient = Iap.getIapClient(activity)
        val task = mClient.createPurchaseIntent(createPurchaseIntentReq(type, productId))
        task.addOnSuccessListener(OnSuccessListener { result ->
            Log.i(TAG,"createPurchaseIntent, onSuccess")
            if (result == null) {
                Log.e(TAG,"result is null")
                return@OnSuccessListener
            }
            val status = result.status
            if (status == null) {
                Log.e(TAG,"status is null")
                return@OnSuccessListener
            }
            // you should pull up the page to complete the payment process.
            if (status.hasResolution()) {
                try {
                    status.startResolutionForResult(activity,REQ_CODE_BUY)
                } catch (exp: SendIntentException) {
                    Log.e(TAG,exp.message.toString())
                }
            } else {
                Log.e(TAG,"intent is null")
            }
        }).addOnFailureListener { e ->
            Log.e(TAG, e.message.toString())
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            if (e is IapApiException) {
                val returnCode = e.statusCode
                Log.e(TAG,"createPurchaseIntent, returnCode: $returnCode")
                // handle error scenarios
            }
        }
    }

    /**
     * Create a PurchaseIntentReq instance.
     * @param type In-app product type.
     * @param productId ID of the in-app product to be paid.
     * The in-app product ID is the product ID you set during in-app product configuration in AppGallery Connect.
     * @return PurchaseIntentReq
     */
    private fun createPurchaseIntentReq(type: Int,productId: String?): PurchaseIntentReq? {
        val req = PurchaseIntentReq()
        req?.let {  productDetails ->
            productDetails.productId=productId
            productDetails.priceType=type
            productDetails.developerPayload="test"
        }
        return req
    }

    override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_BUY) {
            if (data == null) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                return
            }
            val purchaseResultInfo =Iap.getIapClient(this).parsePurchaseResultInfoFromIntent(data)
            when (purchaseResultInfo.returnCode) {
                OrderStatusCode.ORDER_STATE_SUCCESS -> {
                    // verify signature of payment results.
                    val success: Boolean = CipherUtil.doCheck(purchaseResultInfo.inAppPurchaseData,purchaseResultInfo.inAppDataSignature,resources.getString(R.string.publickey))
                    if (success) {
                        // Call the consumeOwnedPurchase interface to consume it after successfully delivering the product to your user.
                        consumeOwnedPurchase(this, purchaseResultInfo.inAppPurchaseData)
                    } else {
                        Toast.makeText(this, "Pay successful,sign failed", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                OrderStatusCode.ORDER_STATE_CANCEL -> {
                    // The User cancels payment.
                    Toast.makeText(this, "user cancel", Toast.LENGTH_SHORT).show()
                    return
                }
                OrderStatusCode.ORDER_PRODUCT_OWNED -> {
                    // The user has already owned the product.
                    Toast.makeText(this, "you have owned the product", Toast.LENGTH_SHORT).show()
                    // you can check if the user has purchased the product and decide whether to provide goods
                    // if the purchase is a consumable product, consuming the purchase and deliver product
                    return
                }
                else -> Toast.makeText(this, "Pay failed", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }

    /**
     * Consume the unconsumed purchase with type 0 after successfully delivering the product, then the Huawei payment server will update the order status and the user can purchase the product again.
     * @param inAppPurchaseData JSON string that contains purchase order details.
     */
    private fun consumeOwnedPurchase(context: Context,inAppPurchaseData: String) {
        Log.i(TAG,"call consumeOwnedPurchase")
        val mClient = Iap.getIapClient(context)
        val task =mClient.consumeOwnedPurchase(createConsumeOwnedPurchaseReq(inAppPurchaseData))
        task.addOnSuccessListener { // Consume success
            Log.i(TAG,"consumeOwnedPurchase success")
            Toast.makeText(context,"Pay success, and the product has been delivered",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Log.e(TAG, e.message.toString())
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            if (e is IapApiException) {
                val apiException = e
                val returnCode = apiException.statusCode
                Log.e(TAG,"consumeOwnedPurchase fail,returnCode: $returnCode")
            } else {
                // Other external errors
            }
        }
    }

    /**
     * Create a ConsumeOwnedPurchaseReq instance.
     * @param purchaseData JSON string that contains purchase order details.
     * @return ConsumeOwnedPurchaseReq
     */
    private fun createConsumeOwnedPurchaseReq(purchaseData: String): ConsumeOwnedPurchaseReq? {
        val req = ConsumeOwnedPurchaseReq()
        // Parse purchaseToken from InAppPurchaseData in JSON format.
        try {
            val inAppPurchaseData = InAppPurchaseData(purchaseData)
            req.purchaseToken = inAppPurchaseData.purchaseToken
        } catch (e: JSONException) {
            Log.e(TAG,"createConsumeOwnedPurchaseReq JSONExeption")
        }
        return req
    }
}
