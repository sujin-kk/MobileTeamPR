package com.example.mobileteampr

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.mobileteampr.databinding.FragmentGroupDialogBinding
import com.google.firebase.database.*


class GroupDialogFragment : DialogFragment() {
    var binding : FragmentGroupDialogBinding?=null
    val myViewModel:MyViewModel by activityViewModels()
    val invitedUsers : ArrayList<String> = ArrayList()// User 아이디 담을 리스트
    lateinit var rdb: DatabaseReference
    lateinit var urdb : DatabaseReference
    lateinit var curId:String

    override fun onResume() {
        super.onResume()
        // full-screen
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false // 밖에 눌러도 dismiss 되지 않음
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // dialog?.window?.requestFeature(STYLE_NO_TITLE)
        binding = FragmentGroupDialogBinding.inflate(layoutInflater, container, false)
        init(binding!!.root)
        return binding!!.root
    }

    private fun init(view:View){
        urdb = FirebaseDatabase.getInstance().getReference("All/Users") // id 검색해서 초대
        rdb = FirebaseDatabase.getInstance().getReference("All/Groups") // groups leaf에 그룹 생성

        // 문제 : null 값 들어감
        curId = myViewModel.curUserId.value.toString() // 현재 계정 정보

        invitedUsers.add(curId) // 자신도 추가

        // 초대 리스트 inflate
        val listAdapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_list_item_1, invitedUsers)
        binding!!.apply{

            userList.adapter = listAdapter
            userList.choiceMode = ListView.CHOICE_MODE_SINGLE

            val title = titleEdit.text.toString() // 사용자 입력 그룹명

            // 친구 검색
            inviteBtn.setOnClickListener {
                urdb.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (user in snapshot.children) { // Users의 키값 탐색
                            if (inviteEdit.text.toString() == user.key.toString()) {
                                invitedUsers.add(user.key.toString())
                                listAdapter.notifyDataSetChanged()
                                inviteEdit.text.clear()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(activity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            submitBtn.setOnClickListener {
                Log.d("cur", curId)
                for(i in invitedUsers)
                    Log.d("invite", i)
                createGroup(title, invitedUsers)
            }

            cancelBtn.setOnClickListener {
                dismiss() // 취소
            }

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun createGroup(title: String, invitedUsers: ArrayList<String>){
        rdb.child(curId).child(title).setValue(invitedUsers) // id - 그룹명 - 그룹참여자들
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}