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
package com.huawei.iap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.huawei.iap.R
import com.huawei.iap.adapters.ProductsListAdapter.MyViewHolder
import com.huawei.iap.callbacks.ProductItemClick
import com.huawei.iap.models.ProductsListModel
import kotlinx.android.synthetic.main.item_productslist.view.*

class ProductsListAdapter(var names: List<ProductsListModel>,private val productItemClick: ProductItemClick) : RecyclerView.Adapter<MyViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_productslist, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        myViewHolder.bindItems(names[i])
    }

    override fun getItemCount(): Int {
        return if (names == null) 0 else names.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class MyViewHolder(itemView: View) : ViewHolder(itemView) {

        fun bindItems(productModel: ProductsListModel){
            productModel?.let {  productDetails ->{
                itemView.item_name.text = productModel.name
                itemView.item_price.text = productModel.price
                itemView.item_image.setImageResource(productModel.image)

            }
            }
            itemView.setOnClickListener {
                productItemClick.onClick(productModel)
            }
        }
    }
}