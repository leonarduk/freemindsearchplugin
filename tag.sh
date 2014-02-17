#!/bin/bash
TAG=${1?-Usage $0 TAG e.g. $0 0.3}
PROJECT=freemindsearchplugin
svn cp  svn+ssh://leonarduk@svn.code.sf.net/p/${PROJECT}/code/trunk  svn+ssh://leonarduk@svn.code.sf.net/p/${PROJECT}/code/tags/$TAG
