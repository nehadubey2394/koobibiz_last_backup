package com.mualab.org.biz.modules.profile_setup.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.profile_setup.BusinessProfileActivity;
import com.mualab.org.biz.model.Category;
import com.mualab.org.biz.model.SubCategory;

import java.util.ArrayList;
import java.util.List;

import views.AnimatedExpandableListView;

public class CategoriesFragmentCreation extends ProfileCreationBaseFragment {

    private AnimatedExpandableListView lvExp;
    private ExpandableListAdapter adapter;
    private List<Category> _listDataHeader;

    public static CategoriesFragmentCreation newInstance() {
        return new CategoriesFragmentCreation();
    }

    public void updateView(List<Category> _listDataHeader){
        this._listDataHeader.clear();
        this._listDataHeader.addAll(_listDataHeader);

        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _listDataHeader = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_categories, container, false);
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvExp = view.findViewById(R.id.lvExp);
        setupAdapter();
    }

    private void setupAdapter(){
        adapter = new ExpandableListAdapter(mContext){
            int lastExpandedGroupPosition = -1;
            @Override
            public void onGroupExpanded(int groupPosition){
                //collapse the old expanded group, if not the same
                //as new group to expand
                if(groupPosition != lastExpandedGroupPosition){
                    lvExp.collapseGroup(lastExpandedGroupPosition);
                }

                if (_listDataHeader.get(groupPosition).subCategories.size()==0)
                    lvExp.setGroupIndicator(null);
                else
                    lvExp.setGroupIndicator(getResources().getDrawable(R.drawable.custom_expandable));

                super.onGroupExpanded(groupPosition);
                lastExpandedGroupPosition = groupPosition;
            }
        };

        lvExp.setAdapter(adapter);
        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        lvExp.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            // int previousGroup = -1;
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
                if (lvExp.isGroupExpanded(groupPosition)) {
                    lvExp.collapseGroupWithAnimation(groupPosition);
                } else {
                    lvExp.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });

        lvExp.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                if(listener!=null && activity!=null){
                    ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                    PagerAdapter adapter = (getActivity()) != null ? activity.mPagerAdapter : null;
                    if (adapter != null && viewPager!=null) {
                        SubCategoriesFragment fragment = (SubCategoriesFragment)
                                adapter.instantiateItem(null, viewPager.getCurrentItem()+1);
                        SubCategory subCategory = _listDataHeader.get(groupPosition).subCategories.get(childPosition);
                        fragment.updateSubCategory(subCategory);
                        //listener.saveTmpData("subCategory", subCategory);
                        listener.onNext();
                    }
                }
                return false;
            }
        });
    }

    class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private Context _context;
        // private List<Category> _listDataHeader; // header titles

        private ExpandableListAdapter(Context context) {
            this._context = context;
            //  this._listDataHeader = _listDataHeader;
        }

        @Override
        public SubCategory getChild(int groupPosition, int childPosititon) {
            // return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
            return _listDataHeader.get(groupPosition).subCategories.get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final SubCategory subCategory = getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.layout_subcategorylist_item, null);
            }

            TextView txtListChild = convertView.findViewById(R.id.lblListItem);
            txtListChild.setText(subCategory.name);
            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return _listDataHeader.get(groupPosition).subCategories.size();
        }

        @Override
        public Category getGroup(int groupPosition) {
            return _listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return _listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final Category category = getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.layout_categorylist_group, null);
            }

            if (_listDataHeader.get(groupPosition).subCategories.size()==0)
                lvExp.setGroupIndicator(null);
            else
                lvExp.setGroupIndicator(getResources().getDrawable(R.drawable.custom_expandable));


            TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
            lblListHeader.setText(category.name);

            convertView.findViewById(R.id.ivAddSubcategory).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                    if(listener!=null && activity!=null){
                        ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                        PagerAdapter adapter = (getActivity()) != null ? activity.mPagerAdapter : null;
                        if (adapter != null && viewPager!=null) {
                            SubCategoriesFragment fragment = (SubCategoriesFragment) adapter.instantiateItem(null, viewPager.getCurrentItem()+1);
                            fragment.addSubCategory(category);
                            listener.onNext();
                        }
                    }
                }
            });

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
}
