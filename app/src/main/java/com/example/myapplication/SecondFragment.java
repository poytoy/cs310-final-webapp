package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentSecondBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    Handler helloHandler=new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(@NonNull Message msg){
            String webMassage =msg.obj.toString();
            return true;
        }
    });
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText commentEditText = view.findViewById(R.id.textInputEditText);
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        binding.floatingActionButton2.setOnClickListener(v -> {
            HelloRepo repo = new HelloRepo();
            WebExampleApp application = (WebExampleApp) requireActivity().getApplication();
            String comment = commentEditText.getText().toString();
            repo.postComment(application.getSrv(), helloHandler,comment,"CS 306","Database Systems");
        });
        TextView commentView = view.findViewById(R.id.textView2);
        commentView.setText("Comments");
        // Inside your Fragment or Activity
        HelloRepo repo = new HelloRepo();
        WebExampleApp application = (WebExampleApp) requireActivity().getApplication();

// Assume you have a TextView with id "textView2" in your layout

        repo.getComments(application.getSrv(), new HelloRepo.CommentCallback() {
            @Override
            public void onCommentsReceived(List<Comment> comments) {
                getActivity().runOnUiThread(() -> {
                    StringBuilder commentsText = new StringBuilder();
                    for (Comment comment : comments) {
                        commentsText.append(comment.getComment()).append("\n");
                    }

                    commentView.setText(commentsText.toString());
                    commentView.invalidate();
                });
            }
        }, "CS 306");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}