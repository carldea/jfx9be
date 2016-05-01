#Windows
clean ; compile ; package ; run


# MacOS,Linux
#  Ordinary way of building a module and launching application
./clean && ./compile && ./package && ./run

#  Creating a runtime image to run as an executable
./clean && ./compile && ./package && ./create_rt_image && ./run_mod_image
