package com.example.sharm_000.homework5;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.PopupMenu;

import java.util.HashMap;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    LinearLayoutManager layoutManager;
    MovieData movieData = new MovieData();
    int layoutType = 1;
    private OnCardItemClickedListener onCardItemClickedListener;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        onCardItemClickedListener = (OnCardItemClickedListener) getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        //layout manager
        if(layoutType==1)
            layoutManager = new LinearLayoutManager(getActivity());
        else
            layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        //specify adapter

        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), movieData.getMoviesList(),layoutType);
        recyclerView.setAdapter(recyclerViewAdapter);
        //adapterAnimation();
        //itemAnimator();

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onCardItemClickedListener.onCardItemClicked(movieData.getItem(position),view);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //int newPos = position + 1;
                //movieData.addItem(newPos, (HashMap<String, ?>) movieData.getItem(position).clone());
                //recyclerViewAdapter.notifyItemInserted(newPos);
                getActivity().startActionMode(new ActionBarCallBack(position));
            }

            @Override
            public void onOverFlowButtonClick(View view, final int position) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.delete:
                                movieData.removeItem(position);
                                recyclerViewAdapter.notifyItemRemoved(position);
                                return true;
                            case R.id.duplicate:
                                movieData.addItem(position + 1, (HashMap<String, ?>) movieData.getItem(position).clone());
                                recyclerViewAdapter.notifyItemInserted(position + 1);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater menuInflater = popup.getMenuInflater();
                menuInflater.inflate(R.menu.contextual_action_bar_menu, popup.getMenu());
                popup.show();
            }

            /*@Override
            public void onItemCheckBoxSelect(int position, boolean isChecked) {
                if (isChecked) {
                    movieData.getItem(position).put("isSelected", true);
                }
                else
                    movieData.getItem(position).put("isSelected", false);
            }*/
        });


        /*selectAll = (Button) rootView.findViewById(R.id.selectAll);
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<movieData.getSize();i++){
                    movieData.getItem(i).put("isSelected", true);
                    recyclerViewAdapter.notifyItemChanged(i);
                }
            }
        });

        clearAll = (Button) rootView.findViewById(R.id.clearAll);
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<movieData.getSize();i++){
                    movieData.getItem(i).put("isSelected", false);
                    recyclerViewAdapter.notifyItemChanged(i);
                }
            }
        });

        delete = (Button) rootView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = movieData.getSize();
                for (int i = 0; i < size; i++) {
                    boolean selectFlag = (Boolean) movieData.getItem(i).get("isSelected");
                    if (selectFlag) {
                        movieData.removeItem(i);
                        recyclerViewAdapter.notifyItemRemoved(i);
                        i--;
                        size--;
                    }
                }
                if(size<=5)
                    recyclerViewAdapter.notifyDataSetChanged();
            }
        });*/


        return rootView;
    }

    private void itemAnimator() {
        if(layoutType==1) {
            SlideInRightAnimator animator = new SlideInRightAnimator();
            animator.setInterpolator(new OvershootInterpolator());
            animator.setAddDuration(500);
            animator.setRemoveDuration(500);
            recyclerView.setItemAnimator(animator);
        }else{
            FadeInAnimator animator = new FadeInAnimator();
            animator.setInterpolator(new OvershootInterpolator());
            animator.setAddDuration(800);
            animator.setRemoveDuration(500);
            recyclerView.setItemAnimator(animator);
        }
    }

    private void adapterAnimation() {
        if(layoutType==1) {
            ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(recyclerViewAdapter);
            alphaAdapter.setDuration(500);
            recyclerView.setAdapter(alphaAdapter);
        }else{
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(recyclerViewAdapter);
            alphaAdapter.setDuration(500);
            recyclerView.setAdapter(alphaAdapter);
        }

    }

    public static RecyclerViewFragment newInstance() {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnCardItemClickedListener{
        public void onCardItemClicked(HashMap<String, ?> movie, View view);
    }

    public void deleteAllMovies(){
        int size = movieData.getSize();
        for (int i = 0; i < size; i++) {
            movieData.removeItem(i);
            recyclerViewAdapter.notifyItemRemoved(i);
            i--;
            size--;
        }
        if(size<=5)
            recyclerViewAdapter.notifyDataSetChanged();
    }

    public void changeToGridView(){
        if(layoutType==1)
            layoutType = 2;
        else if(layoutType==2)
            layoutType=1;
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu.findItem(R.id.actionSearch)==null){
            inflater.inflate(R.menu.recycler_view_menu,menu);
        }
        SearchView search = (SearchView) menu.findItem(R.id.actionSearch).getActionView();
        if(search!=null){
            search.setSubmitButtonEnabled(true);
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    int position = movieData.findFirst(query);
                    if (position >= 0) {
                        recyclerView.scrollToPosition(position);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class ActionBarCallBack implements android.view.ActionMode.Callback{
        int position;
        public ActionBarCallBack(int position){
            this.position=position;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_action_bar_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            HashMap md = movieData.getItem(position);
            mode.setTitle((String) md.get("name"));
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            switch (id){
                case R.id.delete:
                    movieData.removeItem(position);
                    recyclerViewAdapter.notifyItemRemoved(position);
                    mode.finish();
                    break;
                case R.id.duplicate:
                    movieData.addItem(position + 1, (HashMap<String, ?>) movieData.getItem(position).clone());
                    recyclerViewAdapter.notifyItemInserted(position + 1);
                    mode.finish();
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}

