package com.example.grocerymanagement.presentation.adapter.chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.R
import io.noties.markwon.Markwon



class ChatAdapter(
    private val messages: MutableList<Message>,
    private val markwon: Markwon
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_SERVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = if (viewType == VIEW_TYPE_USER) {
            layoutInflater.inflate(R.layout.item_message_user, parent, false)
        } else {
            layoutInflater.inflate(R.layout.item_message_server, parent, false)
        }
        return MessageViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        if (message.isUser) {
            holder.textViewMessage.text = message.text
        } else {
            if (message.isLoading) {
                holder.loadingIndicator?.visibility = View.VISIBLE
                holder.textViewMessage.visibility = View.GONE
            } else {
                holder.loadingIndicator?.visibility = View.GONE
                holder.textViewMessage.visibility = View.VISIBLE
                markwon.setMarkdown(holder.textViewMessage, message.text)
            }
        }
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    class MessageViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        val textViewMessage: TextView = itemView.findViewById(R.id.textViewMessage)
        val loadingIndicator: View? =
            if (viewType == VIEW_TYPE_SERVER) itemView.findViewById(R.id.loadingIndicator) else null
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_SERVER = 2
    }
}
